package com.example.finshot.bulletin.service.post;

import com.example.finshot.bulletin.payload.request.post.CreatePostRequest;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.post.GetPostsResponse;
import com.example.finshot.bulletin.payload.response.post.ReadPostResponse;
import com.example.finshot.bulletin.payload.response.post.UpdatePostRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    CustomSuccessResponse<GetPostsResponse> getMyPosts(String email, Pageable pageable);
    CustomSuccessResponse<String> createPost(CreatePostRequest request, MultipartFile postFile, String email);
    CustomSuccessResponse<String> updatePost(Long postId, UpdatePostRequest request, MultipartFile postFile, String email);
    String deletePost(Long postId, String email);
    CustomSuccessResponse<GetPostsResponse> getAllPosts(Pageable pageable);
    CustomSuccessResponse<ReadPostResponse> readPost(String slug);
}
