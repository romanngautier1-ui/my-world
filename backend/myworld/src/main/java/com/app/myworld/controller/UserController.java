package com.app.myworld.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.app.myworld.dto.authdto.AuthResponse;
import com.app.myworld.dto.userdto.ChangePasswordRequest;
import com.app.myworld.dto.userdto.UserResponse;
import com.app.myworld.dto.userdto.UserUpdateRequest;
import com.app.myworld.service.AuthService;
import com.app.myworld.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Long id = userService.getCurrentUserId();
        try {
            return ResponseEntity.ok(userService.get(id));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @PatchMapping("/me/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long id = userService.getCurrentUserId();
        try {
            return ResponseEntity.ok(userService.changePassword(id, request.newPassword(), request.oldPassword()));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PatchMapping("/me/update-current-user")
    public ResponseEntity<AuthResponse> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        Long id = userService.getCurrentUserId();
        try {
            userService.update(id, request);
            AuthResponse authResponse = authService.generateNewToken(id);
            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }


    @DeleteMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete() {
        Long id = userService.getCurrentUserId();
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }
}
