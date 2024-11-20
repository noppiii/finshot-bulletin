package com.example.finshot.bulletin.payload.response.post;

import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.PostFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class GetPostsResponse {

    private List<PostDto> posts;
    private int totalPages;
    private long totalElements;
    private int currentPage;

    public static GetPostsResponse of(Page<Post> postPage) {
        List<PostDto> postDtos = postPage.getContent().stream()
                .map(PostDto::of)
                .toList();

        return new GetPostsResponse(
                postDtos,
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                postPage.getNumber()
        );
    }

    @Getter
    @Builder
    public static class PostDto {
        private Long id;
        private String title;
        private String slug;
        private String shortContent;
        private String nickname;
        private long viewCount;
        private String imageUrl;
        private List<String> tags;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public static PostDto of(Post post) {
            String firstImageUrl = post.getFiles().stream()
                    .findFirst()
                    .map(PostFile::getUrl)
                    .orElse(null);

            List<String> tagNames = post.getTags().stream()
                    .map(postTag -> postTag.getTag().getName())
                    .toList();

            return builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .slug(post.getSlug())
                    .shortContent(truncateContent(post.getContent()))
                    .viewCount(post.getViewCount())
                    .imageUrl(firstImageUrl)
                    .nickname(post.getWriter().getNickname())
                    .tags(tagNames)
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build();
        }

        private static String truncateContent(String content) {
            if (content == null || content.isEmpty()) {
                return "No content available.";
            }
            String[] words = content.split("\\s+");
            return String.join(" ", Arrays.copyOf(words, Math.min(words.length, 20))) + (words.length > 20 ? "..." : "");
        }
    }
}
