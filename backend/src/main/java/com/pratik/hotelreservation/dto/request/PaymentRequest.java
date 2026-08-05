package com.pratik.hotelreservation.dto.request;

import com.pratik.hotelreservation.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull
    private Long reservationId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull
    private PaymentMethod paymentMethod;

}