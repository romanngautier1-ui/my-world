package com.app.myworld.event;

public record AddChapterEvent
(
    Long chapterId,
    String chapterTitle
) {}
