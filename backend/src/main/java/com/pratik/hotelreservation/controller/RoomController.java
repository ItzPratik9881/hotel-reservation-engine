package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import com.pratik.hotelreservation.dto.request.RoomCreateRequest;
import com.pratik.hotelreservation.dto.request.RoomUpdateRequest;
import com.pratik.hotelreservation.dto.response.RoomResponse;
import com.pratik.hotelreservation.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ApiResponse<RoomResponse> createRoom(
            @Valid @RequestBody RoomCreateRequest request) {

        return ApiResponse.success(
                "Room created successfully",
                roomService.createRoom(request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> getRoomById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Room fetched successfully",
                roomService.getRoomById(id)
        );
    }

    @GetMapping
    public ApiResponse<List<RoomResponse>> getAllRooms() {

        return ApiResponse.success(
                "Rooms fetched successfully",
                roomService.getAllRooms()
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ApiResponse<List<RoomResponse>> getRoomsByHotel(
            @PathVariable Long hotelId) {

        return ApiResponse.success(
                "Rooms fetched successfully",
                roomService.getRoomsByHotel(hotelId)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomUpdateRequest request) {

        return ApiResponse.success(
                "Room updated successfully",
                roomService.updateRoom(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRoom(
            @PathVariable Long id) {

        roomService.deleteRoom(id);

        return ApiResponse.success(
                "Room deleted successfully",
                "Deleted"
        );
    }
}