package com.example.finshot.bulletin.init;

import com.example.finshot.bulletin.entity.*;
import com.example.finshot.bulletin.repository.PostRepository;
import com.example.finshot.bulletin.repository.PostTagRepository;
import com.example.finshot.bulletin.repository.TagRepository;
import com.example.finshot.bulletin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitData implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = userRepository.save(
                User.of(
                        "mnoviantoanggoro@gmail.com",
                        "@Vengeance123",
                        "Novi",
                        "user-profile/default-profile.png"
                )
        );

        Tag tag1 = tagRepository.save(Tag.builder().name("Java").build());
        Tag tag2 = tagRepository.save(Tag.builder().name("Spring").build());
        Tag tag3 = tagRepository.save(Tag.builder().name("Programming").build());

        Post post = Post.builder()
                .writer(user)
                .title("Study spring boot with me")
                .slug("study-spring-boot-with-me")
                .content("This is a guide on how to get started with Spring Boot.")
                .viewCount(0)
                .build();
        postRepository.save(post);

        addTagToPost(post, tag1);
        addTagToPost(post, tag2);
        addTagToPost(post, tag3);

        postRepository.save(post);

        post.getFiles().add(PostFile.builder().post(post).url("posts/2024_11_20/8f59092c-6dc0-4ef9-9594-4ea935d966e6.jpg").build());
        post.getFiles().add(PostFile.builder().post(post).url("posts/2024_11_20/8f59092c-6dc0-4ef9-9594-4ea935d966e6.jpg").build());
        postRepository.save(post);
    }

    private void addTagToPost(Post post, Tag tag) {
        boolean exists = postTagRepository.existsByPostAndTag(post, tag);

        if (!exists) {
            PostTag postTag = PostTag.builder().post(post).tag(tag).build();
            post.getTags().add(postTag);
            postTagRepository.save(postTag);
        }
    }
}
