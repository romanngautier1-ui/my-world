package com.app.myworld.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.myworld.dto.commentdto.CommentCreateRequest;
import com.app.myworld.dto.commentdto.CommentResponse;
import com.app.myworld.dto.commentdto.CommentUpdateRequest;
import com.app.myworld.exception.DuplicateCommentException;
import com.app.myworld.mapper.CommentMapper;
import com.app.myworld.model.Comment;
import com.app.myworld.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentResponse> list() {
        return commentRepository.findAll().stream().map(commentMapper::toResponse).toList();
    }

    @Override
    public CommentResponse get(Long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));
    }

    @Override
    @Transactional
    public CommentResponse create(CommentCreateRequest request) {
        if (commentRepository.existsByUser_IdAndChapter_Id(request.userId(), request.chapterId())) {
            throw new DuplicateCommentException("3 Un commentaire existe déjà pour cet utilisateur et ce chapitre");
        }
        Comment comment = commentMapper.toEntity(request);
        try {
            comment = commentRepository.save(comment);
            return commentMapper.toResponse(comment);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateCommentException("2 Un commentaire existe déjà pour cet utilisateur et ce chapitre", ex);
        }
    }

    @Override
    @Transactional
    public CommentResponse update(Long id, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));

        if (request.content() == null && request.rating() == null) {
            throw new IllegalArgumentException("No fields to update");
        }
        commentMapper.updateEntityFromRequest(request, comment);
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));
        commentRepository.delete(comment);  
    }

    @Override
    public boolean existsByUserIdAndChapterId(Long userId, Long chapterId) {
        return commentRepository.existsByUser_IdAndChapter_Id(userId, chapterId);
    }
}
