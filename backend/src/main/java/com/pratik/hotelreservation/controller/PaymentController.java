package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import com.pratik.hotelreservation.dto.request.PaymentRequest;
import com.pratik.hotelreservation.dto.response.PaymentResponse;
import com.pratik.hotelreservation.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<PaymentResponse> makePayment(
            @Valid @RequestBody PaymentRequest request) {

        return ApiResponse.success(
                "Payment completed successfully",
                paymentService.makePayment(request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> getPaymentById(
            @PathVariable Long id) {

        return ApiResponse.success(
                "Payment fetched successfully",
                paymentService.getPaymentById(id)
        );
    }

    @GetMapping
    public ApiResponse<List<PaymentResponse>> getAllPayments() {

        return ApiResponse.success(
                "Payments fetched successfully",
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/reservation/{reservationId}")
    public ApiResponse<List<PaymentResponse>> getPaymentsByReservation(
            @PathVariable Long reservationId) {

        return ApiResponse.success(
                "Reservation payments fetched successfully",
                paymentService.getPaymentsByReservation(reservationId)
        );
    }
}