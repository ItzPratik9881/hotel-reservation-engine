package com.pratik.hotelreservation.dto.response;

import com.pratik.hotelreservation.enums.PaymentMethod;
import com.pratik.hotelreservation.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {

    private Long id;

    private Long reservationId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String transactionId;

    private LocalDateTime paidAt;

}