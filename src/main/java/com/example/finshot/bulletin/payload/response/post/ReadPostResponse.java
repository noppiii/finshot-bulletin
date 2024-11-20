package com.example.finshot.bulletin.payload.response.post;

import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.PostFile;
import com.example.finshot.bulletin.entity.PostTag;
import com.example.finshot.bulletin.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ReadPostResponse {

    private Long postId;
    private String title;
    private String slug;
    private String writerNickname;
    private List<String> tags;
    private String imageUrl;
    private String content;
    private long viewCount;
    private LocalDateTime createdAt;

    public static ReadPostResponse of(Post post, List<PostTag> postTagMappings) {
        User writer = post.getWriter();

        String firstImageUrl = post.getFiles().stream()
                .findFirst()
                .map(PostFile::getUrl)
                .orElse(null);

        return builder()
                .postId(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .imageUrl(firstImageUrl)
                .writerNickname(writer.getNickname())
                .tags(postTagMappings.stream()
                        .map(postTag -> postTag.getTag().getName())
                        .collect(Collectors.toList()))
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
