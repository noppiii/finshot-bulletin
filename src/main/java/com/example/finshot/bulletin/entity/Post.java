package com.example.finshot.bulletin.entity;

import com.example.finshot.bulletin.entity.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    private long viewCount;

    private long likeCount;

    @Builder
    private Post(User writer, String title, String slug, String content, long viewCount, long likeCount) {
        this.writer = writer;
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
    }

    public void modify(String title, String content, String slug) {
        this.title = title;
        this.content = content;
        this.slug = slug;
    }

}