package com.app.myworld.controller;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.myworld.dto.authdto.AuthResponse;
import com.app.myworld.dto.authdto.LoginRequest;
import com.app.myworld.dto.authdto.RegisterRequest;
import com.app.myworld.dto.authdto.ResetPasswordRequest;
import com.app.myworld.event.OnRegistrationCompleteEvent;
import com.app.myworld.model.User;
import com.app.myworld.repository.UserRepository;
import com.app.myworld.service.AuthService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody RegisterRequest request) {        
        User user = authService.register(request);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return ResponseEntity.ok("Registration successful. Please check your email for verification.");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        String result = authService.validateVerificationToken(token);
        if ("valid".equals(result)) {
            return ResponseEntity.ok("Email verified successfully. You can now log in.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired verification token.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Validated @RequestBody String email) {
        userRepository.findByEmail(email)
            .ifPresent(authService::resetPassword);

        return ResponseEntity.ok(
            "If an account with that email exists, a password reset link has been sent."
        );
    }

    @PostMapping("/reset-password-link")
    public ResponseEntity<String> updatePassword(@Validated @RequestBody ResetPasswordRequest request) {
        String result = authService.updatePassword(request.token(), request.newPassword());
        if ("success".equals(result)) {
            return ResponseEntity.ok("Password updated successfully. You can now log in with your new password.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired password reset token.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails user) {
        authService.logout(user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
