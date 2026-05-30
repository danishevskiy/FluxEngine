package com.fluxengine.controller;

import com.fluxengine.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserService userService;

    @Value("${fluxengine.app.base-url:}")
    private String configuredBaseUrl;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.registerPublicUser(username, email, password, baseUrl(request));
            redirectAttributes.addFlashAttribute("success", "Реєстрацію створено. Перевірте email для підтвердження доступу.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, RedirectAttributes redirectAttributes) {
        boolean ok = userService.verifyEmail(token);
        if (ok) {
            redirectAttributes.addFlashAttribute("success", "Email підтверджено. Тепер можна увійти.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Посилання підтвердження недійсне або прострочене.");
        }
        return "redirect:/login";
    }

    private String baseUrl(HttpServletRequest request) {
        if (configuredBaseUrl != null && !configuredBaseUrl.isBlank()) {
            return configuredBaseUrl.replaceAll("/$", "");
        }
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null || scheme.isBlank()) {
            scheme = request.getScheme();
        }
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) {
            host = request.getServerName() + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
        }
        return scheme + "://" + host;
    }
}
