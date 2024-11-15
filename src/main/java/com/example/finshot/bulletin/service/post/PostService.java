package com.example.finshot.bulletin.service.post;

import com.example.finshot.bulletin.payload.request.post.CreatePostRequest;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.post.GetMyPostsResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    CustomSuccessResponse<GetMyPostsResponse> getMyPosts(String email);
    CustomSuccessResponse<String> createPost(CreatePostRequest request, MultipartFile postFile, String email);
}
