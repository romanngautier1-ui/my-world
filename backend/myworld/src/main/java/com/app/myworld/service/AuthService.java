package com.app.myworld.service;

import com.app.myworld.dto.authdto.AuthResponse;
import com.app.myworld.dto.authdto.LoginRequest;
import com.app.myworld.dto.authdto.RegisterRequest;
import com.app.myworld.model.User;

public interface AuthService {
    
    User register(RegisterRequest request);

    String validateVerificationToken(String token);

    AuthResponse login(LoginRequest request);

    String resetPassword(User user);

    String updatePassword(String token, String newPassword);

    AuthResponse generateNewToken(Long userId);

    void logout(String username);
}
