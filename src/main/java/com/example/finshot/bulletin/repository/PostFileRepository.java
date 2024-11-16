package com.example.finshot.bulletin.repository;

import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    void deleteByPost(Post post);

    @Modifying
    @Query("UPDATE PostFile pf SET pf.post = null WHERE pf.post = :post AND pf.url NOT IN :urls")
    void setPostNullWherePostAndUrlNotIn(@Param("post") Post post, @Param("urls") List<String> urls);

    @Modifying
    @Query("UPDATE PostFile pf SET pf.post = :post WHERE pf.post IS NULL AND pf.url IN :urls")
    void setPostWherePostNullAndUrlIn(@Param("post") Post post, @Param("urls") List<String> urls);
}
