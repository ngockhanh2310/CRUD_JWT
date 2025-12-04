package com.khanh.exercise_jwts.repository;

import com.khanh.exercise_jwts.entity.Post;
import com.khanh.exercise_jwts.entity.User;
import com.khanh.exercise_jwts.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByStatus(PostStatus status);
    List<Post> findAllByAuthor(User author);

    // Delete all posts authored by a given user (used before deleting the user)
    void deleteAllByAuthor(User author);
}
