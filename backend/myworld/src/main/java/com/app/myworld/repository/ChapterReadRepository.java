package com.app.myworld.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.myworld.model.ChapterRead;

@Repository
public interface ChapterReadRepository extends JpaRepository<ChapterRead, Long>{

    boolean existsByUser_IdAndChapter_Id(Long userId, Long chapterId);
}
