package com.khanh.exercise_jwts.dto.post;

import com.khanh.exercise_jwts.enums.PostStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PostDto(
        Long id,
        String title,
        String content,
        PostStatus status,
        Long authorId,
        String authorUsername,
        Instant createdAt,
        Instant updatedAt
) {
}
