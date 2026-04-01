package com.app.myworld.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.app.myworld.event.AddArticleEvent;
import com.app.myworld.model.EmailDetails;
import com.app.myworld.model.User;
import com.app.myworld.repository.UserRepository;
import com.app.myworld.service.EmailService;

@Component
public class AddArticleListener {

    private final EmailService emailService;
    private final UserRepository userRepository;

    public AddArticleListener(EmailService emailService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @EventListener
    public void handleAddArticleEvent(AddArticleEvent event) {
        try {
            for (User user : userRepository.findAllByRoleAndIsActiveTrue(com.app.myworld.model.Role.USER)) {
                EmailDetails details = new EmailDetails();
                details.setRecipient(user.getEmail());
                details.setSubject("New article added: " + event.articleTitle());
                details.setMessage(
                        "A new article (" + event.articleTitle() + ") has been added on MyWorld."
                                + "\n\n"
                                + "You can view it here: /articles/" + event.articleId()
                );
                emailService.sendEmail(details);
            }

        } catch (Exception e) {
            // Log the exception (you can use a logging framework like SLF4J)
            System.err.println("Error while sending email notification: " + e.getMessage());
        }
    }
}

