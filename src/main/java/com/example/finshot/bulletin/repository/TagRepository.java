package com.example.finshot.bulletin.repository;

import com.example.finshot.bulletin.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByNameIn(List<String> tagNames);
}
