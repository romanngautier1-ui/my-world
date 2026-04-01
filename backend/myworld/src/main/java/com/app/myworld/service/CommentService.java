package com.app.myworld.service;

import java.util.List;

import com.app.myworld.dto.commentdto.CommentCreateRequest;
import com.app.myworld.dto.commentdto.CommentResponse;
import com.app.myworld.dto.commentdto.CommentUpdateRequest;

public interface CommentService {
    
    List<CommentResponse> list();

    CommentResponse get(Long id);

    CommentResponse create(CommentCreateRequest request);

    CommentResponse update(Long id, CommentUpdateRequest request);

    void delete(Long id);

    boolean existsByUserIdAndChapterId(Long userId, Long chapterId);
}
