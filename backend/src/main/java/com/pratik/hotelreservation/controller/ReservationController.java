package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import com.pratik.hotelreservation.dto.request.ReservationCreateRequest;
import com.pratik.hotelreservation.dto.response.ReservationResponse;
import com.pratik.hotelreservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(
        name = "Reservations",
        description = "Reservation management and guest lifecycle APIs"
)
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
            summary = "Create a reservation",
            description = "Creates a new hotel reservation for a user and room."
    )
    @PostMapping
    public ApiResponse<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationCreateRequest request) {

        return ApiResponse.success(
                "Reservation created successfully",
                reservationService.createReservation(request)
        );
    }

    @Operation(
            summary = "Get reservation by ID",
            description = "Fetches a reservation using its unique ID."
    )
    @GetMapping("/{id}")
    public ApiResponse<ReservationResponse> getReservationById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Reservation fetched successfully",
                reservationService.getReservationById(id)
        );
    }

    @Operation(
            summary = "Get all reservations",
            description = "Returns all reservations in the system."
    )
    @GetMapping
    public ApiResponse<List<ReservationResponse>> getAllReservations() {

        return ApiResponse.success(
                "Reservations fetched successfully",
                reservationService.getAllReservations()
        );
    }

    @Operation(
            summary = "Get reservations by user",
            description = "Returns all reservations associated with a specific user."
    )
    @GetMapping("/user/{userId}")
    public ApiResponse<List<ReservationResponse>> getReservationsByUser(
            @PathVariable Long userId) {

        return ApiResponse.success(
                "User reservations fetched successfully",
                reservationService.getReservationsByUser(userId)
        );
    }

    @Operation(
            summary = "Get reservations by room",
            description = "Returns all reservations associated with a specific room."
    )
    @GetMapping("/room/{roomId}")
    public ApiResponse<List<ReservationResponse>> getReservationsByRoom(
            @PathVariable Long roomId) {

        return ApiResponse.success(
                "Room reservations fetched successfully",
                reservationService.getReservationsByRoom(roomId)
        );
    }

    @Operation(
            summary = "Cancel a reservation",
            description = "Cancels an existing reservation."
    )
    @PutMapping("/{id}/cancel")
    public ApiResponse<String> cancelReservation(
            @PathVariable Long id) {

        reservationService.cancelReservation(id);

        return ApiResponse.success(
                "Reservation cancelled successfully",
                "Cancelled"
        );
    }

    @Operation(
            summary = "Check in guest",
            description = "Checks in a guest for a confirmed reservation."
    )
    @PutMapping("/{id}/check-in")
    public ApiResponse<String> checkIn(
            @PathVariable Long id) {

        reservationService.checkIn(id);

        return ApiResponse.success(
                "Guest checked in successfully",
                "CHECKED_IN"
        );
    }

    @Operation(
            summary = "Check out guest",
            description = "Checks out a guest from a checked-in reservation."
    )
    @PutMapping("/{id}/check-out")
    public ApiResponse<String> checkOut(
            @PathVariable Long id) {

        reservationService.checkOut(id);

        return ApiResponse.success(
                "Guest checked out successfully",
                "CHECKED_OUT"
        );
    }
}