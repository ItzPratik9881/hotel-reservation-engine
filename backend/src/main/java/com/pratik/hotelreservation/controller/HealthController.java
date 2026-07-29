package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<String> health() {

        return ApiResponse.<String>builder()
                .success(true)
                .message("Application is running successfully")
                .data("Hotel Reservation Engine API")
                .timestamp(LocalDateTime.now())
                .build();
    }
}