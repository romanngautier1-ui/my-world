package com.app.myworld.dto.bookdto;

import java.time.LocalDateTime;

public record BookListResponse(
    Long id,
    String title,
    Integer number,
    String description,
    LocalDateTime createdAt,
    Long chapterCount,
    String urlImage
) {}
