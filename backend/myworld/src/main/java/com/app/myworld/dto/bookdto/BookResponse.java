package com.app.myworld.dto.bookdto;

import java.time.LocalDateTime;
import java.util.List;

import com.app.myworld.dto.chapterdto.ChapterListResponse;

public record BookResponse(
    Long id,
    String title,
    Integer number,
    String description,
    String urlImage,
    LocalDateTime createdAt,
    List<ChapterListResponse> chapters
) {}
