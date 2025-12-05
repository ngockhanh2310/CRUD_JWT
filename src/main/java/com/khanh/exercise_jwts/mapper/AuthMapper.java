package com.khanh.exercise_jwts.mapper;

import com.khanh.exercise_jwts.dto.request.RegisterRequest;
import com.khanh.exercise_jwts.dto.response.RegisterResponse;
import com.khanh.exercise_jwts.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toUserRegister(RegisterRequest request);

    @Mapping(target = "roles", source = "roles")
    RegisterResponse toRegisterResponse(User user);
}
