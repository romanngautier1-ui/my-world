package com.app.myworld.dto.userdto;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
    Long id,
    String username,
    String email,
    LocalDateTime createdAt,
    Boolean isActive,
    List<UserChapterReadResponse> lastReads
) {}
