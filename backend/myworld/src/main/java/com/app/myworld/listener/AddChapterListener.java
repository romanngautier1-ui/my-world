package com.app.myworld.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.app.myworld.event.AddChapterEvent;
import com.app.myworld.model.EmailDetails;
import com.app.myworld.model.Role;
import com.app.myworld.model.User;
import com.app.myworld.repository.UserRepository;
import com.app.myworld.service.EmailService;

@Component
public class AddChapterListener {
    
    private final EmailService emailService;
    private final UserRepository userRepository;

    public AddChapterListener(EmailService emailService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @EventListener
    public void handleAddChapterEvent(AddChapterEvent event) {
        try {
            // Notify all active non-admin users when a new chapter is added.
            for (User user : userRepository.findAllByRoleAndIsActiveTrue(Role.USER)) {
                EmailDetails details = new EmailDetails();
                details.setRecipient(user.getEmail());
                details.setSubject("New chapter added: " + event.chapterTitle());
                details.setMessage(
                        "A new chapter (" + event.chapterTitle() + ") has been added on MyWorld."
                                + "\n\n"
                                + "You can view it here: /chapters/" + event.chapterId()
                );
                emailService.sendEmail(details);
            }

        } catch (Exception e) {
            // Log the exception (you can use a logging framework like SLF4J)
            System.err.println("Error while sending email notification: " + e.getMessage());
        }
    }
}
