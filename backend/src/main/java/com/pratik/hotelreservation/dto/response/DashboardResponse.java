package com.pratik.hotelreservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long totalHotels;

    private long totalRooms;

    private long totalUsers;

    private long totalReservations;

    private long confirmedReservations;

    private long cancelledReservations;

    private long checkedInReservations;

    private long checkedOutReservations;

    private BigDecimal totalRevenue;

    private long availableRooms;

    private long occupiedRooms;
}