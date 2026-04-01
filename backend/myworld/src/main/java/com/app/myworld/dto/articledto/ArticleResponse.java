package com.app.myworld.dto.articledto;

import java.time.LocalDateTime;

public record ArticleResponse(
    Long id,
    String title,
    String content,
    String username,
    String urlImage,
    LocalDateTime createdAt
) {}