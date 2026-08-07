package com.pratik.hotelreservation.service;

import com.pratik.hotelreservation.dto.response.BookingAnalyticsResponse;
import com.pratik.hotelreservation.dto.response.DashboardResponse;
import com.pratik.hotelreservation.dto.response.RevenueResponse;

public interface DashboardService {

    DashboardResponse getDashboard();

    RevenueResponse getRevenue();

    BookingAnalyticsResponse getBookingAnalytics();
}