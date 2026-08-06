package com.pratik.hotelreservation.dto.request;

import com.pratik.hotelreservation.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private Long reservationId;

    @NotNull
    private PaymentMethod paymentMethod;

}