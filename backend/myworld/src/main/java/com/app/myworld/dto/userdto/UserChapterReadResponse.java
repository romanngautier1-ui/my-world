package com.app.myworld.dto.userdto;

import java.time.LocalDateTime;

public record UserChapterReadResponse(
    Long chapterId,
    String chapterTitle,
    Integer bookNumber,
    LocalDateTime readAt
) {}
