package com.example.finshot.bulletin.repository;

import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByWriterOrderByIdDesc(User writer);
    boolean existsBySlug(String slug);
}
