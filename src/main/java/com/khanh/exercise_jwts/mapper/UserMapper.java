package com.khanh.exercise_jwts.mapper;

import com.khanh.exercise_jwts.dto.user.UserDto;
import com.khanh.exercise_jwts.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(toRoleNames(user))")
    UserDto toDto(User user);

    default Set<String> toRoleNames(User user) {
        if (user.getRoles() == null) return null;
        return user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
    }
}
