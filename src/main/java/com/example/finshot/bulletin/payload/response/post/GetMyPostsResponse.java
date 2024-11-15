package com.example.finshot.bulletin.payload.response.post;

import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetMyPostsResponse {

    private List<PostDto> posts;

    public static GetMyPostsResponse of(List<Post> posts) {
        List<PostDto> postDtos = posts.stream()
                .map(PostDto::of).toList();

        return new GetMyPostsResponse(postDtos);
    }

    @Getter
    @Builder
    private static class PostDto {

        private Long postId;
        private String title;
        private Long writerId;
        private String writerNickname;
        private String writerRole;
        private LocalDateTime createdAt;

        private static PostDto of(Post post) {
            User writer = post.getWriter();
            return builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .writerId(writer.getId())
                    .writerNickname(writer.getNickname())
                    .writerRole(writer.getRole().name())
                    .createdAt(post.getCreatedAt())
                    .build();
        }
    }
}

