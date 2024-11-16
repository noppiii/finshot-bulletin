package com.example.finshot.bulletin.repository;

import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Modifying
    @Query("DELETE FROM PostTag pt WHERE pt.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
