package com.khanh.exercise_jwts.service;

import com.khanh.exercise_jwts.dto.post.PostCreateRequest;
import com.khanh.exercise_jwts.dto.post.PostDto;
import com.khanh.exercise_jwts.entity.Post;
import com.khanh.exercise_jwts.entity.User;
import com.khanh.exercise_jwts.enums.PostStatus;
import com.khanh.exercise_jwts.enums.Role;
import com.khanh.exercise_jwts.mapper.PostMapper;
import com.khanh.exercise_jwts.repository.PostRepository;
import com.khanh.exercise_jwts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

    public List<PostDto> listApproved() {
        return postRepository.findAllByStatus(PostStatus.APPROVED).stream().map(postMapper::toDto).toList();
    }

    public List<PostDto> listAllForAdmin() {
        return postRepository.findAll().stream().map(postMapper::toDto).toList();
    }

    public List<PostDto> listMine() {
        return postRepository.findAllByAuthor(currentUser()).stream().map(postMapper::toDto).toList();
    }

    public PostDto create(PostCreateRequest req) {
        Post p = Post.builder()
                .title(req.title())
                .content(req.content())
                .status(PostStatus.PENDING)
                .author(currentUser())
                .build();
        return postMapper.toDto(postRepository.save(p));
    }

    public PostDto update(Long id, PostCreateRequest req) {
        Post p = postRepository.findById(id).orElseThrow();
        User me = currentUser();
        boolean isOwner = p.getAuthor().getId().equals(me.getId());
        boolean isAdmin = me.getRoles().contains(Role.ROLE_ADMIN);
        if (!isOwner && !isAdmin) throw new AccessDeniedException("Not allowed");
        p.setTitle(req.title());
        p.setContent(req.content());
        // After update by owner, set back to pending for re-approval unless admin
        if (!isAdmin) p.setStatus(PostStatus.PENDING);
        return postMapper.toDto(postRepository.save(p));
    }

    public void delete(Long id) {
        Post p = postRepository.findById(id).orElseThrow();
        User me = currentUser();
        boolean isOwner = p.getAuthor().getId().equals(me.getId());
        boolean isAdmin = me.getRoles().contains(Role.ROLE_ADMIN);
        if (!isOwner && !isAdmin) throw new AccessDeniedException("Not allowed");
        postRepository.delete(p);
    }

    public PostDto approve(Long id) {
        Post p = postRepository.findById(id).orElseThrow();
        p.setStatus(PostStatus.APPROVED);
        return postMapper.toDto(postRepository.save(p));
    }

    public PostDto reject(Long id) {
        Post p = postRepository.findById(id).orElseThrow();
        p.setStatus(PostStatus.REJECTED);
        return postMapper.toDto(postRepository.save(p));
    }
}
