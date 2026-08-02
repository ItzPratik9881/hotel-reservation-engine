package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import com.pratik.hotelreservation.dto.request.HotelCreateRequest;
import com.pratik.hotelreservation.dto.request.HotelUpdateRequest;
import com.pratik.hotelreservation.dto.response.HotelResponse;
import com.pratik.hotelreservation.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ApiResponse<HotelResponse> createHotel(
            @Valid @RequestBody HotelCreateRequest request) {

        return ApiResponse.success(
                "Hotel created successfully",
                hotelService.createHotel(request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<HotelResponse> getHotelById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Hotel fetched successfully",
                hotelService.getHotelById(id)
        );
    }

    @GetMapping
    public ApiResponse<List<HotelResponse>> getAllHotels() {

        return ApiResponse.success(
                "Hotels fetched successfully",
                hotelService.getAllHotels()
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<HotelResponse> updateHotel(
            @PathVariable Long id,
            @Valid @RequestBody HotelUpdateRequest request) {

        return ApiResponse.success(
                "Hotel updated successfully",
                hotelService.updateHotel(id, request)
        );
    }

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