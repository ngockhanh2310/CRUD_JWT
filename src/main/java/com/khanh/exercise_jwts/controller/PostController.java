package com.khanh.exercise_jwts.controller;

import com.khanh.exercise_jwts.dto.post.PostCreateRequest;
import com.khanh.exercise_jwts.dto.post.PostDto;
import com.khanh.exercise_jwts.dto.response.ApiResponse;
import com.khanh.exercise_jwts.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "Public: list approved posts")
    @GetMapping("/public")
    public ApiResponse<List<PostDto>> publicList() {
        List<PostDto> dto = postService.listApproved();
        return ApiResponse.<List<PostDto>>builder()
                .success(true)
                .message("List approved posts")
                .data(dto)
                .build();
    }

    @Operation(summary = "User: list my posts")
    @GetMapping("/me")
    public ApiResponse<List<PostDto>> myPosts() {
        List<PostDto> dto = postService.listMine();
        return ApiResponse.<List<PostDto>>builder()
                .success(true)
                .message("List my posts")
                .data(dto)
                .build();
    }

    @Operation(summary = "Admin: list all posts")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<PostDto>> all() {
        List<PostDto> dto = postService.listAllForAdmin();
        return ApiResponse.<List<PostDto>>builder()
                .success(true)
                .message("List all posts")
                .data(dto)
                .build();
    }

    @Operation(summary = "User: create a post (becomes PENDING)")
    @PostMapping
    public ApiResponse<PostDto> create(@Valid @RequestBody PostCreateRequest req) {
        PostDto dto = postService.create(req);
        return ApiResponse.<PostDto>builder()
                .success(true)
                .message("Create a post success")
                .data(dto)
                .build();
    }

    @Operation(summary = "User/Admin: update a post")
    @PutMapping("/{id}")
    public ApiResponse<PostDto> update(@PathVariable Long id, @Valid @RequestBody PostCreateRequest req) {
        PostDto dto = postService.update(id, req);
        return ApiResponse.<PostDto>builder()
                .success(true)
                .message("Update a post success")
                .data(dto)
                .build();
    }

    @Operation(summary = "User/Admin: delete a post")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Delete a post success")
                .build();
    }

    @Operation(summary = "Manager/Admin: approve a post")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ApiResponse<PostDto> approve(@PathVariable Long id) {
        PostDto dto = postService.approve(id);
        return ApiResponse.<PostDto>builder()
                .success(true)
                .message("Approve a post success")
                .data(dto)
                .build();
    }

    @Operation(summary = "Manager/Admin: reject a post")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ApiResponse<PostDto> reject(@PathVariable Long id) {
        PostDto dto = postService.reject(id);
        return ApiResponse.<PostDto>builder()
                .success(true)
                .message("Reject a post success")
                .data(dto)
                .build();
    }
}
