package com.app.myworld.dto.chapterdto;

import java.time.LocalDateTime;
import java.util.List;

import com.app.myworld.dto.bookdto.BookSummaryResponse;
import com.app.myworld.dto.commentdto.CommentResponse;

public record ChapterResponse(
    Long id,
    String title,
    Integer number,
    String content,
    BookSummaryResponse book,
    LocalDateTime createdAt,
    List<CommentResponse> comments,
    Integer like
) {}
