package com.app.myworld.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.myworld.dto.userdto.UserCreateRequest;
import com.app.myworld.dto.userdto.UserResponse;
import com.app.myworld.dto.userdto.UserUpdateRequest;
import com.app.myworld.mapper.UserMapper;
import com.app.myworld.model.User;
import com.app.myworld.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse get(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        User user = userMapper.toEntity(request);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (request.password() == null && request.username() == null) {
            throw new IllegalArgumentException("No fields to update");
        }

        userMapper.updateEntityFromRequest(request, user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public String changePassword(Long id, String newPassword, String oldPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password changed successfully";
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        userRepository.delete(user);
    }

    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }
        String username = auth.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("Authenticated user not found")).getId();
    }
}
