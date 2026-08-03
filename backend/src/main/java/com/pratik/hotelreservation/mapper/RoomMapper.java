package com.pratik.hotelreservation.mapper;

import com.pratik.hotelreservation.dto.request.RoomCreateRequest;
import com.pratik.hotelreservation.dto.request.RoomUpdateRequest;
import com.pratik.hotelreservation.dto.response.RoomResponse;
import com.pratik.hotelreservation.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "available", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Room toEntity(RoomCreateRequest request);

    @Mapping(source = "hotel.id", target = "hotelId")
    RoomResponse toResponse(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(RoomUpdateRequest request,
                      @MappingTarget Room room);
}