package com.app.myworld.service;

import java.util.List;

import com.app.myworld.dto.chapterreaddto.ChapterReadCreateRequest;
import com.app.myworld.dto.chapterreaddto.ChapterReadResponse;

public interface ChapterReadService {
    
    List<ChapterReadResponse> list();

    ChapterReadResponse create(ChapterReadCreateRequest request);

    void delete(Long id);

    boolean existsByUserIdAndChapterId(Long userId, Long chapterId);
}
