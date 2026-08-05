package com.pratik.hotelreservation.service;

import com.pratik.hotelreservation.dto.request.PaymentRequest;
import com.pratik.hotelreservation.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse makePayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByReservation(Long reservationId);

}