package com.app.myworld.service;

import org.springframework.stereotype.Service;

import com.app.myworld.model.User;
import com.app.myworld.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final UserRepository userRepository;

    @Override
    public void createVerificationToken(String token, User user) {
        user.setVerificationToken(token);
        userRepository.save(user);
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
    
}
