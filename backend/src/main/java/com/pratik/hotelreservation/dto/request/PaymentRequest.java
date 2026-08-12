package com.pratik.hotelreservation.dto.request;

import com.pratik.hotelreservation.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Reservation ID is required")
    @Positive(message = "Reservation ID must be positive")
    private Long reservationId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}