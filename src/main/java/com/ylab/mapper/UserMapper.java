package com.ylab.mapper;

import com.ylab.dto.UserDTO;
import com.ylab.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "default")
public interface UserMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "isAdmin", source = "isAdmin")
    @Mapping(target = "isBlocked", source = "isBlocked")
    UserDTO toDto(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "isAdmin", source = "isAdmin")
    @Mapping(target = "isBlocked", source = "isBlocked")
    User toEntity(UserDTO userDTO);
}