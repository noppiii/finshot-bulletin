package com.example.finshot.bulletin.service.post.impl;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.*;
import com.example.finshot.bulletin.exception.CustomException;
import com.example.finshot.bulletin.payload.request.post.CreatePostRequest;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.post.GetPostsResponse;
import com.example.finshot.bulletin.payload.response.post.ReadPostResponse;
import com.example.finshot.bulletin.payload.response.post.UpdatePostRequest;
import com.example.finshot.bulletin.repository.*;
import com.example.finshot.bulletin.service.post.PostFileService;
import com.example.finshot.bulletin.service.post.PostService;
import com.example.finshot.bulletin.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public CustomSuccessResponse<GetPostsResponse> getMyPosts(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        Page<Post> posts = postRepository.findAllByWriterOrderByIdDesc(user, pageable);
        GetPostsResponse getPostsResponse = GetPostsResponse.of(posts);

        return new CustomSuccessResponse<>("200", "Successfully get you posts", getPostsResponse);
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

        return new CustomSuccessResponse<>("200", "Create post successfully", null);
    }


    @Override
    public CustomSuccessResponse<String> updatePost(Long postId, UpdatePostRequest request, MultipartFile postFile, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getWriter().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        String newSlug = SlugUtils.generateSlug(user.getNickname(), "post", request.getTitle());
        int counter = 1;
        String originalSlug = newSlug;
        while (postRepository.existsBySlug(newSlug)) {
            newSlug = originalSlug + "-" + counter;
            counter++;
        }

        if (postFile != null && !postFile.isEmpty()) {
            postFileRepository.deleteByPost(post);
            String filePath = postFileService.upload(postFile, email);
            PostFile newPostFile = PostFile.builder()
                    .url(filePath)
                    .post(post)
                    .build();
            postFileRepository.save(newPostFile);
        }
        postTagRepository.deleteAllByPost(post);
        List<Tag> existingTags = tagRepository.findByNameIn(request.getTags());
        List<Tag> newTags = request.getTags().stream()
                .filter(tagName -> existingTags.stream().noneMatch(tag -> tag.getName().equals(tagName)))
                .map(tagName -> Tag.builder().name(tagName).build())
                .collect(Collectors.toList());
        tagRepository.saveAll(newTags);
        existingTags.addAll(newTags);
        existingTags.forEach(tag -> {
            PostTag postTag = PostTag.builder()
                    .post(post)
                    .tag(tag)
                    .build();
            postTagRepository.save(postTag);
        });
        post.modify(request.getTitle(), request.getContent(), newSlug);
        postRepository.save(post);

        return new CustomSuccessResponse<>("200", "Successfully update post", null);
    }

    @Override
    public String deletePost(Long postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getWriter().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        postTagRepository.deleteAllByPost(post);

        post.getFiles().forEach(postFile -> postFileService.deleteFile(postFile.getUrl()));
        postFileRepository.deleteByPost(post);

        String postTitle = post.getTitle();
        postRepository.delete(post);

        return postTitle;
    }

    @Override
    public CustomSuccessResponse<GetPostsResponse> getAllPosts(Pageable pageable) {
        Page<Post> postsPage = postRepository.findAll(pageable);

        GetPostsResponse responseContent = GetPostsResponse.of(postsPage);

        return new CustomSuccessResponse<>(
                "200",
                "Successfully get all post",
                responseContent
        );
    }

    @Override
    public CustomSuccessResponse<ReadPostResponse> readPost(String slug) {
        Post post = postRepository.findPost(slug)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<PostTag> postTags = postTagRepository.findPostTags(post);

        postRepository.increaseViewCount(post);

        ReadPostResponse readPostResponse = ReadPostResponse.of(post, postTags);
        return new CustomSuccessResponse<>("200", "Successfully get post", readPostResponse);
    }

}

