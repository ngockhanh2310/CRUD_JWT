package com.khanh.exercise_jwts.mapper;

import com.khanh.exercise_jwts.dto.post.PostDto;
import com.khanh.exercise_jwts.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    PostDto toDto(Post post);
}
