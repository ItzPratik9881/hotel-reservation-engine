package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.request.PaymentRequest;
import com.pratik.hotelreservation.dto.response.PaymentResponse;
import com.pratik.hotelreservation.entity.Payment;
import com.pratik.hotelreservation.entity.Reservation;
import com.pratik.hotelreservation.enums.BookingStatus;
import com.pratik.hotelreservation.enums.PaymentStatus;
import com.pratik.hotelreservation.exception.BusinessException;
import com.pratik.hotelreservation.exception.ResourceNotFoundException;
import com.pratik.hotelreservation.mapper.PaymentMapper;
import com.pratik.hotelreservation.repository.PaymentRepository;
import com.pratik.hotelreservation.repository.ReservationRepository;
import com.pratik.hotelreservation.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse makePayment(PaymentRequest request) {

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reservation not found"));

        // Prevent duplicate successful payments
        if (paymentRepository.existsByReservationAndPaymentStatus(
                reservation,
                PaymentStatus.SUCCESS)) {

            throw new BusinessException(
                    "Payment has already been completed for this reservation");
        }

        // Prevent payment for cancelled reservations
        if (reservation.getBookingStatus() == BookingStatus.CANCELLED) {

            throw new BusinessException(
                    "Cannot make payment for a cancelled reservation");
        }

        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(reservation.getTotalPrice())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId(UUID.randomUUID().toString())
                .paidAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update reservation status after successful payment
        reservation.setBookingStatus(BookingStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reservation not found"));

        return paymentRepository.findByReservation(reservation)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
}