package com.app.myworld.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.myworld.dto.chapterreaddto.ChapterReadCreateRequest;
import com.app.myworld.dto.chapterreaddto.ChapterReadResponse;
import com.app.myworld.mapper.ChapterReadMapper;
import com.app.myworld.model.ChapterRead;
import com.app.myworld.repository.ChapterReadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterReadServiceImpl implements ChapterReadService {
    
    private final ChapterReadRepository chapterReadRepository;
    private final ChapterReadMapper chapterReadMapper;

    @Override
    public List<ChapterReadResponse> list() {
        return chapterReadRepository.findAll().stream().map(chapterReadMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ChapterReadResponse create(ChapterReadCreateRequest request) {
        ChapterRead chapterRead = chapterReadMapper.toEntity(request);
        ChapterRead savedChapterRead = chapterReadRepository.save(chapterRead);
        return chapterReadMapper.toResponse(savedChapterRead);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ChapterRead chapterRead = chapterReadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ChapterRead not found: " + id));
        chapterReadRepository.delete(chapterRead);
    }

    @Override
    public boolean existsByUserIdAndChapterId(Long userId, Long chapterId) {
        return chapterReadRepository.existsByUser_IdAndChapter_Id(userId, chapterId);
    }
}
