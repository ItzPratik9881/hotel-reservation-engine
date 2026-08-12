package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import com.pratik.hotelreservation.dto.request.HotelCreateRequest;
import com.pratik.hotelreservation.dto.request.HotelUpdateRequest;
import com.pratik.hotelreservation.dto.response.HotelResponse;
import com.pratik.hotelreservation.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
@Tag(
        name = "Hotels",
        description = "Hotel management and CRUD APIs"
)
public class HotelController {

    private final HotelService hotelService;

    @Operation(
            summary = "Create a hotel",
            description = "Creates a new hotel in the reservation system."
    )
    @PostMapping
    public ApiResponse<HotelResponse> createHotel(
            @Valid @RequestBody HotelCreateRequest request) {

        return ApiResponse.success(
                "Hotel created successfully",
                hotelService.createHotel(request)
        );
    }

    @Operation(
            summary = "Get hotel by ID",
            description = "Fetches a hotel using its unique ID."
    )
    @GetMapping("/{id}")
    public ApiResponse<HotelResponse> getHotelById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Hotel fetched successfully",
                hotelService.getHotelById(id)
        );
    }

    @Operation(
            summary = "Get all hotels",
            description = "Returns all hotels available in the system."
    )
    @GetMapping
    public ApiResponse<List<HotelResponse>> getAllHotels() {

        return ApiResponse.success(
                "Hotels fetched successfully",
                hotelService.getAllHotels()
        );
    }

    @Operation(
            summary = "Update a hotel",
            description = "Updates the details of an existing hotel."
    )
    @PutMapping("/{id}")
    public ApiResponse<HotelResponse> updateHotel(
            @PathVariable Long id,
            @Valid @RequestBody HotelUpdateRequest request) {

        return ApiResponse.success(
                "Hotel updated successfully",
                hotelService.updateHotel(id, request)
        );
    }

    @Operation(
            summary = "Delete a hotel",
            description = "Deletes an existing hotel from the system."
    )
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteHotel(
            @PathVariable Long id) {

        hotelService.deleteHotel(id);

        return ApiResponse.success(
                "Hotel deleted successfully",
                "Deleted"
        );
    }
}