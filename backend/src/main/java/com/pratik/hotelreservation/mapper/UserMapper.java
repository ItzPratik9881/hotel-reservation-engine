package com.pratik.hotelreservation.mapper;

import com.pratik.hotelreservation.dto.request.RegisterRequest;
import com.pratik.hotelreservation.dto.response.RegisterResponse;
import com.pratik.hotelreservation.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);

    RegisterResponse toResponse(User user);
}