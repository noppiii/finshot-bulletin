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
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(nullable = false)
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @Builder
    private Comment(User writer, Post post, String content, Comment parent) {
        this.writer = writer;
        this.post = post;
        this.content = content;
        this.parent = parent;
    }

    public void modify(String content) {
        this.content = content;
    }
}
