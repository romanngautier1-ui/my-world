package com.app.myworld.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.myworld.dto.chapterdto.ChapterCreateRequest;
import com.app.myworld.dto.chapterdto.ChapterListResponse;
import com.app.myworld.dto.chapterdto.ChapterResponse;
import com.app.myworld.dto.chapterdto.ChapterUpdateRequest;
import com.app.myworld.mapper.ChapterMapper;
import com.app.myworld.model.Chapter;
import com.app.myworld.repository.ChapterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterServiceImpl implements ChapterService {
    
        private final ChapterRepository chapterRepository;
        private final ChapterMapper chapterMapper;

        @Override
        public List<ChapterListResponse> list() {
            return chapterRepository.findAll().stream().map(chapterMapper::toList).sorted((a, b) -> a.number() - b.number()).toList();
        }
    
        @Override
        public ChapterResponse get(Long id) {
            return chapterRepository.findById(id)
                    .map(chapterMapper::toResponse)
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found: " + id));
        }

        @Override
        @Transactional
        public ChapterResponse create(ChapterCreateRequest request) {
            Chapter chapter = chapterMapper.toEntity(request);
            chapter = chapterRepository.save(chapter);
            return chapterMapper.toResponse(chapter);
        }

        @Override
        @Transactional
        public ChapterResponse update(Long id, ChapterUpdateRequest request) {
            Chapter chapter = chapterRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found: " + id));

            if (request.title() == null
                    && request.number() == null
                    && request.content() == null
                    && request.bookId() == null) {
                throw new IllegalArgumentException("No fields to update");
            }

            chapterMapper.updateEntityFromRequest(request, chapter);
            return chapterMapper.toResponse(chapter);
        }

        @Override
        @Transactional
        public ChapterResponse incrementLike(Long id) {
            int updated = chapterRepository.incrementLikeCount(id);
            if (updated == 0) {
                throw new IllegalArgumentException("Chapter not found: " + id);
            }
            return get(id);
        }

        @Override
        @Transactional
        public ChapterResponse decrementLike(Long id) {
            int updated = chapterRepository.decrementLikeCount(id);
            if (updated == 0) {
                throw new IllegalArgumentException("Chapter not found: " + id);
            }
            return get(id);
        }

        @Override
        public Integer[] getNeighbors(Long id) {
            Chapter previous = chapterRepository.findFirstByIdLessThanOrderByIdDesc(id).orElse(null);
            Chapter next = chapterRepository.findFirstByIdGreaterThanOrderByIdAsc(id).orElse(null);
            return new Integer[] {
                previous != null ? previous.getId().intValue() : null,
                next != null ? next.getId().intValue() : null
            };
        }

        @Override
        @Transactional
        public void delete(Long id) {
            Chapter chapter = chapterRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found: " + id));
            chapterRepository.delete(chapter);
        }
}
