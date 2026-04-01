package com.app.myworld.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.myworld.model.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long>{

	@Modifying
	@Query("update Chapter c set c.likeCount = c.likeCount + 1 where c.id = :id")
	int incrementLikeCount(@Param("id") Long id);
	@Modifying
	@Query("update Chapter c set c.likeCount = case when c.likeCount > 0 then c.likeCount - 1 else 0 end where c.id = :id")
	int decrementLikeCount(@Param("id") Long id);
    Optional<Chapter> findFirstByIdLessThanOrderByIdDesc(Long id);
    Optional<Chapter> findFirstByIdGreaterThanOrderByIdAsc(Long id);
}
