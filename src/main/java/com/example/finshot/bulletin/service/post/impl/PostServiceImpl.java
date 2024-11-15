package com.example.finshot.bulletin.service.post.impl;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.*;
import com.example.finshot.bulletin.exception.CustomException;
import com.example.finshot.bulletin.payload.request.post.CreatePostRequest;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.post.GetMyPostsResponse;
import com.example.finshot.bulletin.repository.*;
import com.example.finshot.bulletin.service.post.PostFileService;
import com.example.finshot.bulletin.service.post.PostService;
import com.example.finshot.bulletin.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostFileService postFileService;
    private final PostFileRepository postFileRepository;

    @Override
    public CustomSuccessResponse<GetMyPostsResponse> getMyPosts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        List<Post> posts = postRepository.findAllByWriterOrderByIdDesc(user);
        GetMyPostsResponse getMyPostsResponse = GetMyPostsResponse.of(posts);
        return new CustomSuccessResponse<>("200 OK", "Berhasil mendapatkan data posts anda", getMyPostsResponse);
    }

    @Override
    public CustomSuccessResponse<String> createPost(CreatePostRequest request,  MultipartFile postFile, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        String slug = SlugUtils.generateSlug(user.getNickname(), "post", request.getTitle());

        int counter = 1;
        String originalSlug = slug;
        while (postRepository.existsBySlug(slug)) {
            slug = originalSlug + "-" + counter;
            counter++;
        }

        Post post = Post.builder()
                .writer(user)
                .title(request.getTitle())
                .content(request.getContent())
                .slug(slug)
                .build();
        postRepository.save(post);

        String filePath = null;
        if (postFile != null && !postFile.isEmpty()) {
            filePath = postFileService.upload(postFile, email);
            PostFile postFileEntity = PostFile.builder()
                    .url(filePath)
                    .post(post)
                    .build();
            postFileRepository.save(postFileEntity);
        }

        List<Tag> tags = tagRepository.findByNameIn(request.getTags());

        List<Tag> newTags = request.getTags().stream()
                .filter(tagName -> tags.stream().noneMatch(tag -> tag.getName().equals(tagName)))
                .map(tagName -> Tag.builder().name(tagName).build())
                .collect(Collectors.toList());

        tagRepository.saveAll(newTags);
        tags.addAll(newTags);

        tags.forEach(tag -> {
            PostTag postTag = PostTag.builder()
                    .post(post)
                    .tag(tag)
                    .build();
            postTagRepository.save(postTag);
        });

        return new CustomSuccessResponse<>("200 OK", "Berhasil membuat post", null);
    }
}
