package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import com.pratik.hotelreservation.dto.request.ReservationCreateRequest;
import com.pratik.hotelreservation.dto.response.ReservationResponse;
import com.pratik.hotelreservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ApiResponse<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationCreateRequest request) {

        return ApiResponse.success(
                "Reservation created successfully",
                reservationService.createReservation(request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<ReservationResponse> getReservationById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Reservation fetched successfully",
                reservationService.getReservationById(id)
        );
    }

    @GetMapping
    public ApiResponse<List<ReservationResponse>> getAllReservations() {

        return ApiResponse.success(
                "Reservations fetched successfully",
                reservationService.getAllReservations()
        );
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<ReservationResponse>> getReservationsByUser(
            @PathVariable Long userId) {

        return ApiResponse.success(
                "User reservations fetched successfully",
                reservationService.getReservationsByUser(userId)
        );
    }

    @GetMapping("/room/{roomId}")
    public ApiResponse<List<ReservationResponse>> getReservationsByRoom(
            @PathVariable Long roomId) {

        return ApiResponse.success(
                "Room reservations fetched successfully",
                reservationService.getReservationsByRoom(roomId)
        );
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<String> cancelReservation(
            @PathVariable Long id) {

        reservationService.cancelReservation(id);

        return ApiResponse.success(
                "Reservation cancelled successfully",
                "Cancelled"
        );
    }
}