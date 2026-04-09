package com.app.myworld;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.internet.MimeMessage;

@TestConfiguration
public class NoopMailTestConfig {

    @Bean
    JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl() {
            @Override
            public void send(MimeMessage mimeMessage) {
                // no-op
            }

            @Override
            public void send(MimeMessage... mimeMessages) {
                // no-op
            }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) {
                // no-op
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) {
                // no-op
            }

            @Override
            public void send(SimpleMailMessage simpleMessage) {
                // no-op
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) {
                // no-op
            }
        };
    }
}
