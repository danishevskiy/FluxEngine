package com.fluxengine.service;

import com.fluxengine.model.AppUser;
import com.fluxengine.model.EmailVerificationToken;
import com.fluxengine.model.UserRole;
import com.fluxengine.repository.AppUserRepository;
import com.fluxengine.repository.EmailVerificationTokenRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private final AppUserRepository users;
    private final EmailVerificationTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${fluxengine.security.username:admin}")
    private String defaultAdminUsername;

    @Value("${fluxengine.security.password:admin123}")
    private String defaultAdminPassword;

    @Value("${fluxengine.security.email:admin@local}")
    private String defaultAdminEmail;

    public UserService(AppUserRepository users,
                       EmailVerificationTokenRepository tokens,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.users = users;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostConstruct
    @Transactional
    public void ensureDefaultAdmin() {
        if (!users.findByRolesContaining(UserRole.ADMIN).isEmpty()) {
            return;
        }

        AppUser admin = new AppUser();
        admin.setUsername(defaultAdminUsername);
        admin.setEmail(defaultAdminEmail);
        admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
        admin.setEnabled(true);
        admin.setEmailVerified(true);
        admin.getRoles().add(UserRole.ADMIN);
        admin.getRoles().add(UserRole.USER);
        users.save(admin);
    }

    public List<AppUser> findAll() {
        return users.findAll();
    }

    @Transactional
    public AppUser registerPublicUser(String username, String email, String password, String appBaseUrl) {
        AppUser user = createUser(username, email, password, Set.of(UserRole.USER), false, false);
        createAndSendVerification(user, appBaseUrl);
        return user;
    }

    @Transactional
    public AppUser createByAdmin(String username, String email, String password, boolean adminRole, boolean enabled, boolean emailVerified) {
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);
        if (adminRole) {
            roles.add(UserRole.ADMIN);
        }
        return createUser(username, email, password, roles, enabled, emailVerified);
    }

    @Transactional
    public void setEnabled(Long id, boolean enabled) {
        AppUser user = users.findById(id).orElseThrow();
        user.setEnabled(enabled);
        users.save(user);
    }

    @Transactional
    public void delete(Long id) {
        AppUser user = users.findById(id).orElseThrow();
        if (user.getRoles().contains(UserRole.ADMIN) && users.findByRolesContaining(UserRole.ADMIN).size() <= 1) {
            throw new IllegalStateException("Не можна видалити останнього адміністратора");
        }
        tokens.deleteByUser(user);
        users.delete(user);
    }

    @Transactional
    public boolean verifyEmail(String tokenValue) {
        EmailVerificationToken token = tokens.findByToken(tokenValue).orElse(null);
        if (token == null || token.getUsedAt() != null || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        AppUser user = token.getUser();
        user.setEmailVerified(true);
        user.setEnabled(true);
        users.save(user);

        token.setUsedAt(LocalDateTime.now());
        tokens.save(token);
        return true;
    }

    private AppUser createUser(String username, String email, String password, Set<UserRole> roles, boolean enabled, boolean emailVerified) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();

        if (normalizedUsername.length() < 3) {
            throw new IllegalArgumentException("Логін має містити щонайменше 3 символи");
        }
        if (normalizedEmail.isBlank() || !normalizedEmail.contains("@")) {
            throw new IllegalArgumentException("Некоректний email");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Пароль має містити щонайменше 6 символів");
        }
        if (users.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new IllegalArgumentException("Такий логін уже існує");
        }
        if (users.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Такий email уже існує");
        }

        AppUser user = new AppUser();
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(enabled);
        user.setEmailVerified(emailVerified);
        user.setRoles(new HashSet<>(roles));
        return users.save(user);
    }

    private void createAndSendVerification(AppUser user, String appBaseUrl) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString().replace("-", ""));
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        tokens.save(token);

        String link = appBaseUrl + "/verify-email?token=" + token.getToken();
        emailService.sendVerification(user.getEmail(), user.getUsername(), link);
    }
}
