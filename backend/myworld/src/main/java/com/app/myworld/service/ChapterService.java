package com.app.myworld.service;

import java.util.List;

import com.app.myworld.dto.chapterdto.ChapterCreateRequest;
import com.app.myworld.dto.chapterdto.ChapterListResponse;
import com.app.myworld.dto.chapterdto.ChapterResponse;
import com.app.myworld.dto.chapterdto.ChapterUpdateRequest;

public interface ChapterService {
    
    List<ChapterListResponse> list();

    ChapterResponse get(Long id);

    ChapterResponse create(ChapterCreateRequest request);

    ChapterResponse update(Long id, ChapterUpdateRequest request);

    ChapterResponse incrementLike(Long id);

    ChapterResponse decrementLike(Long id);

    Integer[] getNeighbors(Long id);

    void delete(Long id);
}
