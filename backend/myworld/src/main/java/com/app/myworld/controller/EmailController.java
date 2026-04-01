package com.app.myworld.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.myworld.model.EmailDetails;
import com.app.myworld.service.EmailService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/mails")
@RequiredArgsConstructor
@Validated
public class EmailController {
    
    private final EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailDetails details) {
        return emailService.sendEmail(details);
    }

    @PostMapping("/sendWithAttachment")
    public String sendEmailWithAttachment(@RequestBody EmailDetails details) {
        return emailService.sendEmailWithAttachment(details);
    }

    @PostMapping("/receive")
    public String receiveEmail(@RequestBody EmailDetails details) {
        return emailService.receiveEmail(details);
    }
}
