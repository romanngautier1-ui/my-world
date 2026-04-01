package com.app.myworld.dto.bookdto;

public record BookSummaryResponse(
    Long id,
    String title,
    Integer number
) {}
