package com.example.finshot.bulletin.repository;

import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByWriterOrderByIdDesc(User writer, Pageable pageable);

    boolean existsBySlug(String slug);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.tags pt LEFT JOIN FETCH pt.tag LEFT JOIN FETCH p.writer WHERE p.id = :id")
    Optional<Post> findByIdWithTags(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p = :post")
    void increaseViewCount(@Param("post") Post post);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.tags pt LEFT JOIN FETCH pt.tag WHERE p.slug = :slug")
    Optional<Post> findPost(@Param("slug") String slug);
}
