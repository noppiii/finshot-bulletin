package com.example.finshot.bulletin.repository;

import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByWriterOrderByIdDesc(User writer);
    boolean existsBySlug(String slug);
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.tags pt LEFT JOIN FETCH pt.tag WHERE p.id = :id")
    Optional<Post> findByIdWithTags(@Param("id") Long id);
}
