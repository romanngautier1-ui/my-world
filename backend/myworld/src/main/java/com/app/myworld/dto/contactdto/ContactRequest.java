package com.app.myworld.dto.contactdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactRequest {

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    @Size(max = 254, message = "Email must be less than 254 characters")
    private String recipient;

    @NotBlank(message = "Subject is required")
    @Size(max = 254, message = "Subject must be less than 254 characters")
    private String subject;

    @NotBlank(message = "Message body is required")
    @Size(max = 5000, message = "Message must be less than 5000 characters")
    private String message;
}
