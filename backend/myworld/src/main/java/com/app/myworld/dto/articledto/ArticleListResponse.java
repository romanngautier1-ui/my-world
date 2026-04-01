package com.app.myworld.dto.articledto;

import java.time.LocalDateTime;

public record ArticleListResponse(
    Long id,
    String title,
    LocalDateTime createdAt,
    String urlImage
) {}
