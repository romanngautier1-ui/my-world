package com.app.myworld.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.myworld.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
    
    boolean existsByUser_IdAndChapter_Id(Long userId, Long chapterId);
}
