package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import com.pratik.hotelreservation.dto.response.BookingAnalyticsResponse;
import com.pratik.hotelreservation.dto.response.DashboardResponse;
import com.pratik.hotelreservation.dto.response.RevenueResponse;
import com.pratik.hotelreservation.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard() {

        return ApiResponse.success(
                "Dashboard data fetched successfully",
                dashboardService.getDashboard()
        );
    }

    @GetMapping("/revenue")
    public ApiResponse<RevenueResponse> getRevenue() {

        return ApiResponse.success(
                "Revenue data fetched successfully",
                dashboardService.getRevenue()
        );
    }

    @GetMapping("/bookings")
    public ApiResponse<BookingAnalyticsResponse> getBookingAnalytics() {

        return ApiResponse.success(
                "Booking analytics fetched successfully",
                dashboardService.getBookingAnalytics()
        );
    }
}