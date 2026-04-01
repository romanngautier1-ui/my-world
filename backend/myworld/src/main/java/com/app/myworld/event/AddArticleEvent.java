package com.app.myworld.event;

public record AddArticleEvent
(
    Long articleId,
    String articleTitle
) {}
