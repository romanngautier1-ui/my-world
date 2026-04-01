package com.app.myworld.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.app.myworld.dto.commentdto.CommentCreateRequest;
import com.app.myworld.dto.commentdto.CommentResponse;
import com.app.myworld.dto.commentdto.CommentUpdateRequest;
import com.app.myworld.exception.DuplicateCommentException;
import com.app.myworld.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> list() {
        return ResponseEntity.ok(commentService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(commentService.get(id));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByUserIdAndChapterId(
            @RequestParam Long userId,
            @RequestParam Long chapterId) {
        boolean exists = commentService.existsByUserIdAndChapterId(userId, chapterId);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> create(@Valid @RequestBody CommentCreateRequest request) {
        try {
            CommentResponse created = commentService.create(request);
            return ResponseEntity.created(URI.create("/api/comments/" + created.id())).body(created);
        } catch (DuplicateCommentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> update(@PathVariable Long id, @Valid @RequestBody CommentUpdateRequest request) {
        try {
            return ResponseEntity.ok(commentService.update(id, request));
        } catch (IllegalArgumentException ex) {
            HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, ex.getMessage(), ex);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            commentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, ex.getMessage(), ex);
        }
    }
}
