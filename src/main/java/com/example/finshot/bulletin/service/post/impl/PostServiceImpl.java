package com.example.finshot.bulletin.service.post.impl;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.*;
import com.example.finshot.bulletin.exception.CustomException;
import com.example.finshot.bulletin.payload.request.post.CreatePostRequest;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.post.GetMyPostsResponse;
import com.example.finshot.bulletin.payload.response.post.UpdatePostRequest;
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


    @Override
    public CustomSuccessResponse<String> updatePost(Long postId, UpdatePostRequest request, MultipartFile postFile, String email) {
        // Validasi pengguna
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // Pastikan pengguna yang mencoba memperbarui adalah penulis post
        if (!post.getWriter().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // Buat slug baru berdasarkan judul baru
        String newSlug = SlugUtils.generateSlug(user.getNickname(), "post", request.getTitle());
        int counter = 1;
        String originalSlug = newSlug;
        while (postRepository.existsBySlug(newSlug)) {
            newSlug = originalSlug + "-" + counter;
            counter++;
        }

        // Update file post (jika ada file baru yang diunggah)
        if (postFile != null && !postFile.isEmpty()) {
            // Hapus file lama jika ada
            postFileRepository.deleteByPost(post);
            String filePath = postFileService.upload(postFile, email);
            PostFile newPostFile = PostFile.builder()
                    .url(filePath)
                    .post(post)
                    .build();
            postFileRepository.save(newPostFile);
        }

        // Update tags
        postTagRepository.deleteAllByPost(post); // Hapus tag lama
        List<Tag> existingTags = tagRepository.findByNameIn(request.getTags());

        // Pisahkan tag baru dan lama
        List<Tag> newTags = request.getTags().stream()
                .filter(tagName -> existingTags.stream().noneMatch(tag -> tag.getName().equals(tagName)))
                .map(tagName -> Tag.builder().name(tagName).build())
                .collect(Collectors.toList());

        // Simpan tag baru ke database
        tagRepository.saveAll(newTags);
        existingTags.addAll(newTags);

        // Tambahkan hubungan antara post dan tag
        existingTags.forEach(tag -> {
            PostTag postTag = PostTag.builder()
                    .post(post)
                    .tag(tag)
                    .build();
            postTagRepository.save(postTag);
        });

        // Modifikasi data post
        post.modify(request.getTitle(), request.getContent(), newSlug);

        postRepository.save(post);

        return new CustomSuccessResponse<>("200 OK", "Berhasil memperbaruhi post", null);
    }

}
