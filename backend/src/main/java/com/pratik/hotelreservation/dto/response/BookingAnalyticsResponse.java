package com.pratik.hotelreservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAnalyticsResponse {

    private long totalReservations;

    private long confirmedReservations;

    private long cancelledReservations;

    private long checkedInReservations;

    private long checkedOutReservations;
}