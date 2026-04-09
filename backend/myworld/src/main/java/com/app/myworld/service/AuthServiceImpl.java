package com.app.myworld.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.myworld.dto.authdto.AuthResponse;
import com.app.myworld.dto.authdto.LoginRequest;
import com.app.myworld.dto.authdto.RegisterRequest;
import com.app.myworld.model.EmailDetails;
import com.app.myworld.model.Role;
import com.app.myworld.model.User;
import com.app.myworld.repository.UserRepository;
import com.app.myworld.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${app.frontend.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email déjà utilisé");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris");

        String token = UUID.randomUUID().toString();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .verificationToken(token)
                .build();

        userRepository.save(user);

        return user;
    }

    @Override
    public String validateVerificationToken(String token) {
        User user = userRepository.findByVerificationToken(token).orElse(null);
        if (user == null) {
            return "invalid";
        }
        user.setIsActive(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsernameAndIsActiveTrue(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable ou non vérifié"));

        // Backward compat: users created before tokenVersion existed may have null in DB
        if (user.getTokenVersion() == null) {
            user.setTokenVersion(0);
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token).username(user.getUsername())
                .email(user.getEmail()).role(user.getRole())
                .build();
    }

    @Override
    public String resetPassword(User user) {
        String token = UUID.randomUUID().toString();

        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiresAt(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        String verificationLink = normalizeBaseUrl(frontendBaseUrl)
            + "/reset-password-link?token=" + token;

        EmailDetails details = EmailDetails.builder()
            .recipient(user.getEmail())
            .subject("Réinitialisation de votre mot de passe MyWorld")
            .message(
                "Bonjour " + user.getUsername() + ",\n\n"
                    + "Vous avez demandé à réinitialiser votre mot de passe sur MyWorld. Veuillez cliquer sur le lien suivant pour mettre à jour votre mot de passe :\n"
                    + verificationLink
                    + "\n\nCordialement,\nL'équipe MyWorld"
            )
            .build();

        emailService.sendEmail(details);

        return "ok";
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            return "";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @Override
    public String updatePassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token).orElse(null);
        if (user == null) {
            return "invalid";
        }

        LocalDateTime expiresAt = user.getResetPasswordTokenExpiresAt();
        if (expiresAt == null || expiresAt.isBefore(LocalDateTime.now())) {
            user.setResetPasswordToken(null);
            user.setResetPasswordTokenExpiresAt(null);
            userRepository.save(user);
            return "invalid";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiresAt(null);
        userRepository.save(user);
        return "success";
    }

    @Override
    public AuthResponse generateNewToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Integer current = user.getTokenVersion();
        if (current == null) {
            current = 0;
        }
        user.setTokenVersion(current + 1);
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token).username(user.getUsername())
                .email(user.getEmail()).role(user.getRole())
                .build();
    }

    @Override
    public void logout(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Integer current = user.getTokenVersion();
        if (current == null) {
            current = 0;
        }
        user.setTokenVersion(current + 1);
        userRepository.save(user);
    }
}
