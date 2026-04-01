package com.app.myworld.service;

import com.app.myworld.model.User;

public interface VerificationTokenService {

    void createVerificationToken(String token, User user);

    String validateVerificationToken(String token);
}
