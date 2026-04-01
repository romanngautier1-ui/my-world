package com.app.myworld.service;

import com.app.myworld.dto.userdto.UserCreateRequest;
import com.app.myworld.dto.userdto.UserResponse;
import com.app.myworld.dto.userdto.UserUpdateRequest;

public interface UserService {

    UserResponse get(Long id);

    UserResponse create(UserCreateRequest request);

    UserResponse update(Long id, UserUpdateRequest request);

    public String changePassword(Long id, String newPassword, String oldPassword);

    void delete(Long id);

    Long getCurrentUserId();
}
