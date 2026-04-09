package com.app.myworld.service;

import com.app.myworld.dto.contactdto.ContactRequest;
import com.app.myworld.model.EmailDetails;

public interface EmailService {
    
    String sendEmail(EmailDetails details);

    String sendEmailWithAttachment(EmailDetails details);

    String receiveEmail(ContactRequest details);
}
