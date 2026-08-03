package com.pratik.hotelreservation.service;

import com.pratik.hotelreservation.dto.request.RoomCreateRequest;
import com.pratik.hotelreservation.dto.request.RoomUpdateRequest;
import com.pratik.hotelreservation.dto.response.RoomResponse;

import java.util.List;

public interface RoomService {

    RoomResponse createRoom(RoomCreateRequest request);

    RoomResponse getRoomById(Long id);

    List<RoomResponse> getAllRooms();

    List<RoomResponse> getRoomsByHotel(Long hotelId);

    RoomResponse updateRoom(Long id, RoomUpdateRequest request);

    void deleteRoom(Long id);
}