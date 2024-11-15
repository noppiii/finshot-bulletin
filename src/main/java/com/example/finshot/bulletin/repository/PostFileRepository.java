package com.example.finshot.bulletin.repository;

import com.example.finshot.bulletin.entity.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
}
