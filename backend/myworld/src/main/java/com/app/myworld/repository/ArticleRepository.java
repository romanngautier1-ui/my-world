package com.app.myworld.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.myworld.model.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findFirstByIdLessThanOrderByIdDesc(Long id);
    Optional<Article> findFirstByIdGreaterThanOrderByIdAsc(Long id);

}