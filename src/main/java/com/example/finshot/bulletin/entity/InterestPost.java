package com.example.finshot.bulletin.entity;

import com.example.finshot.bulletin.entity.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "post_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class InterestPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder
    private InterestPost(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}