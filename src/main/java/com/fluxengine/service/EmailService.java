package com.fluxengine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSender;

    @Value("${fluxengine.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:no-reply@fluxengine.local}")
    private String from;

    public EmailService(ObjectProvider<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerification(String to, String username, String link) {
        if (!mailEnabled) {
            log.warn("Email sending is disabled. Verification link for {}: {}", to, link);
            return;
        }

        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.warn("JavaMailSender is not configured. Verification link for {}: {}", to, link);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("FluxEngine — підтвердження email");
        message.setText("Вітаємо, " + username + "!\n\n" +
                "Підтвердіть email для FluxEngine за посиланням:\n" + link + "\n\n" +
                "Посилання чинне 24 години.");
        sender.send(message);
    }
}
