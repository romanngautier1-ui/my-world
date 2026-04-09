package com.app.myworld.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.app.myworld.dto.contactdto.ContactRequest;
import com.app.myworld.model.EmailDetails;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendEmail(EmailDetails details) {
        
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMessage());
            mailMessage.setSubject(details.getSubject());
            javaMailSender.send(mailMessage);
            return "Email sent successfully";

        } catch (Exception e) {
            return "Error while sending email: " + e.getMessage();
        }  
    }

    @Override
    public String sendEmailWithAttachment(EmailDetails details) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
    
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());
            helper.setText(details.getMessage());

            safeAttachmentPath(details.getAttachment());
            FileSystemResource file = new FileSystemResource(details.getAttachment());
            helper.addAttachment(file.getFilename(), file);

            javaMailSender.send(mimeMessage);

            return "Email sent successfully";

        } catch (Exception e) {
            return "Error while sending email: " + e.getMessage();
        }
    }

    @Override
    public String receiveEmail(ContactRequest request) {
        try {
            String recipient = rejectCrlf(request.getRecipient());
            String subject = rejectCrlf(request.getSubject());
            String message = rejectCrlf(request.getMessage());
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(sender);
            mailMessage.setReplyTo(recipient);
            mailMessage.setText(message);
            mailMessage.setSubject(subject);
            javaMailSender.send(mailMessage);
            return "Email sent successfully";

        } catch (Exception e) {
            return "Error while sending email: " + e.getMessage();
        }
    }

    public static String rejectCrlf(String s) {
        if (s == null) return null;
        if (s.indexOf('\r') >= 0 || s.indexOf('\n') >= 0) {
            throw new IllegalArgumentException("Email contains CR/LF characters");
        }
        return s;
    }

    public static void safeAttachmentPath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Attachment path cannot be null or empty");
        }
        if (path.contains("..") || path.contains("/") || path.contains("\\")) {
            throw new IllegalArgumentException("Invalid attachment path");
        }
    }
}
