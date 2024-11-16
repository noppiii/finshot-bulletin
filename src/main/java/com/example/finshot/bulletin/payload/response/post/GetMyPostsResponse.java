package com.example.finshot.bulletin.payload.response.post;

import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.PostFile;
import com.example.finshot.bulletin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetMyPostsResponse {

    private List<PostDto> posts;

    public static GetMyPostsResponse of(List<Post> posts) {
        List<PostDto> postDtos = posts.stream()
                .map(PostDto::of)
                .toList();

        return new GetMyPostsResponse(postDtos);
    }

    @Getter
    @Builder
    public static class PostDto {

        private Long id;
        private String title;
        private String shortContent;
        private long viewCount;
        private long likeCount;
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

            String formattedDate = formatDate(post.getCreatedAt());
            String imageUrl = getImageUrl(formattedDate, firstImageUrl);

            return builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .shortContent(truncateContent(post.getContent()))
                    .viewCount(post.getViewCount())
                    .likeCount(post.getLikeCount())
                    .imageUrl(imageUrl)
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

        private static String formatDate(LocalDateTime date) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            return date.format(formatter);
        }

        private static String getImageUrl(String formattedDate, String imageUrl) {
            if (imageUrl == null) {
                return null;
            }

            return "/store/post/" + imageUrl;
        }
    }
}
