package com.app.myworld.listener;

import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.app.myworld.event.OnRegistrationCompleteEvent;
import com.app.myworld.model.EmailDetails;
import com.app.myworld.service.EmailService;

@Component
public class RegisterListener {

    private final EmailService emailService;

    @Value("${app.frontend.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    public RegisterListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        String token = event.getUser().getVerificationToken();
        String verificationLink = normalizeBaseUrl(frontendBaseUrl)
            + "/verify-email?token=" + token;

        EmailDetails details = EmailDetails.builder()
            .recipient(event.getUser().getEmail())
            .subject("Vérification de votre compte MyWorld")
            .message(
                "Bonjour " + event.getUser().getUsername() + ",\n\n"
                    + "Merci de vous être inscrit sur MyWorld ! Veuillez cliquer sur le lien suivant pour vérifier votre compte :\n"
                    + verificationLink
                    + "\n\nCordialement,\nL'équipe MyWorld"
            )
            .build();

        emailService.sendEmail(details);
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            return "";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
