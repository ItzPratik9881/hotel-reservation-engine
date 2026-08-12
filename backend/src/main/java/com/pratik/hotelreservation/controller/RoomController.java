package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import com.pratik.hotelreservation.dto.request.RoomCreateRequest;
import com.pratik.hotelreservation.dto.request.RoomUpdateRequest;
import com.pratik.hotelreservation.dto.response.RoomResponse;
import com.pratik.hotelreservation.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(
        name = "Rooms",
        description = "Room management and availability APIs"
)
public class RoomController {

    private final RoomService roomService;

    @Operation(
            summary = "Create a room",
            description = "Creates a new room for a hotel."
    )
    @PostMapping
    public ApiResponse<RoomResponse> createRoom(
            @Valid @RequestBody RoomCreateRequest request) {

        return ApiResponse.success(
                "Room created successfully",
                roomService.createRoom(request)
        );
    }

    @Operation(
            summary = "Get room by ID",
            description = "Fetches a room using its unique ID."
    )
    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> getRoomById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Room fetched successfully",
                roomService.getRoomById(id)
        );
    }

    @Operation(
            summary = "Get all rooms",
            description = "Returns all rooms available in the system."
    )
    @GetMapping
    public ApiResponse<List<RoomResponse>> getAllRooms() {

        return ApiResponse.success(
                "Rooms fetched successfully",
                roomService.getAllRooms()
        );
    }

    @Operation(
            summary = "Get rooms by hotel",
            description = "Returns all rooms belonging to a specific hotel."
    )
    @GetMapping("/hotel/{hotelId}")
    public ApiResponse<List<RoomResponse>> getRoomsByHotel(
            @PathVariable Long hotelId) {

        return ApiResponse.success(
                "Rooms fetched successfully",
                roomService.getRoomsByHotel(hotelId)
        );
    }

    @Operation(
            summary = "Update a room",
            description = "Updates the details of an existing room."
    )
    @PutMapping("/{id}")
    public ApiResponse<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomUpdateRequest request) {

        return ApiResponse.success(
                "Room updated successfully",
                roomService.updateRoom(id, request)
        );
    }

    @Operation(
            summary = "Delete a room",
            description = "Deletes an existing room from the system."
    )
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