package com.app.myworld.dto.chapterdto;

import java.time.LocalDateTime;

public record ChapterListResponse(
    Long id,
    String title,
    Integer number,
    LocalDateTime createdAt
) {}
