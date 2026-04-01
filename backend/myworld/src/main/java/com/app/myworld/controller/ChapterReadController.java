package com.app.myworld.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.app.myworld.dto.chapterreaddto.ChapterReadCreateRequest;
import com.app.myworld.dto.chapterreaddto.ChapterReadResponse;
import com.app.myworld.service.ChapterReadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chapter-reads")
@RequiredArgsConstructor
@Validated
public class ChapterReadController {
    
    private final ChapterReadService chapterReadService;

    @GetMapping
    public ResponseEntity<List<ChapterReadResponse>> list() {
        return ResponseEntity.ok(chapterReadService.list());
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByUserIdAndChapterId(
            @RequestParam Long userId,
            @RequestParam Long chapterId) {
        boolean exists = chapterReadService.existsByUserIdAndChapterId(userId, chapterId);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<ChapterReadResponse> create(@Valid @RequestBody ChapterReadCreateRequest request) {
        try {
            ChapterReadResponse created = chapterReadService.create(request);
            return ResponseEntity.created(URI.create("/api/chapter-reads/" + created.id())).body(created);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            chapterReadService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
