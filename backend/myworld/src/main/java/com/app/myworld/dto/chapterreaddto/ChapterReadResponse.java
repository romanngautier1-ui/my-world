package com.app.myworld.dto.chapterreaddto;

import java.time.LocalDateTime;

public record ChapterReadResponse(
    Long id,
    String title,
    Integer number,
    LocalDateTime readAt
) {}
