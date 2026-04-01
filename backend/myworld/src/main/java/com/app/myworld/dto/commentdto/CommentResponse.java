package com.app.myworld.dto.commentdto;

import java.time.LocalDateTime;

public record CommentResponse(
    Long id,
    String content,
    Integer rating,
    String username,
    LocalDateTime createdAt
) {}