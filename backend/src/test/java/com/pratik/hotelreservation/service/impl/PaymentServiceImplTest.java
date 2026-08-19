package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.request.PaymentRequest;
import com.pratik.hotelreservation.dto.response.PaymentResponse;
import com.pratik.hotelreservation.entity.Payment;
import com.pratik.hotelreservation.entity.Reservation;
import com.pratik.hotelreservation.enums.BookingStatus;
import com.pratik.hotelreservation.enums.PaymentMethod;
import com.pratik.hotelreservation.enums.PaymentStatus;
import com.pratik.hotelreservation.exception.BusinessException;
import com.pratik.hotelreservation.exception.ResourceNotFoundException;
import com.pratik.hotelreservation.mapper.PaymentMapper;
import com.pratik.hotelreservation.repository.PaymentRepository;
import com.pratik.hotelreservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest request;
    private Reservation reservation;
    private Payment payment;
    private PaymentResponse response;

    @BeforeEach
    void setUp() {

        request = new PaymentRequest();

        request.setReservationId(1L);
        request.setPaymentMethod(PaymentMethod.CARD);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setBookingStatus(BookingStatus.PENDING);
        reservation.setTotalPrice(
                new BigDecimal("6000.00"));

        payment = Payment.builder()
                .reservation(reservation)
                .amount(new BigDecimal("6000.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("test-transaction-id")
                .paidAt(LocalDateTime.now())
                .build();

        response = mock(PaymentResponse.class);
    }

    @Test
    void makePayment_shouldProcessSuccessfully() {

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(paymentRepository
                .existsByReservationAndPaymentStatus(
                        reservation,
                        PaymentStatus.SUCCESS))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment);

        when(paymentMapper.toResponse(any(Payment.class)))
                .thenReturn(response);

        PaymentResponse result =
                paymentService.makePayment(request);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(
                BookingStatus.CONFIRMED,
                reservation.getBookingStatus());

        verify(paymentRepository)
                .save(any(Payment.class));

        verify(reservationRepository)
                .save(reservation);

        verify(paymentMapper)
                .toResponse(any(Payment.class));
    }

    @Test
    void makePayment_shouldThrowExceptionWhenReservationNotFound() {

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.makePayment(request)
        );

        verify(paymentRepository, never())
                .save(any());

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void makePayment_shouldThrowExceptionWhenPaymentAlreadySuccessful() {

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(paymentRepository
                .existsByReservationAndPaymentStatus(
                        reservation,
                        PaymentStatus.SUCCESS))
                .thenReturn(true);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> paymentService.makePayment(request)
                );

        assertEquals(
                "Payment has already been completed for this reservation",
                exception.getMessage());

        verify(paymentRepository, never())
                .save(any());

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void makePayment_shouldThrowExceptionForCancelledReservation() {

        reservation.setBookingStatus(
                BookingStatus.CANCELLED);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(paymentRepository
                .existsByReservationAndPaymentStatus(
                        reservation,
                        PaymentStatus.SUCCESS))
                .thenReturn(false);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> paymentService.makePayment(request)
                );

        assertEquals(
                "Cannot make payment for a cancelled reservation",
                exception.getMessage());

        verify(paymentRepository, never())
                .save(any());

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void makePayment_shouldSetReservationStatusToConfirmed() {

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(paymentRepository
                .existsByReservationAndPaymentStatus(
                        reservation,
                        PaymentStatus.SUCCESS))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment);

        when(paymentMapper.toResponse(any(Payment.class)))
                .thenReturn(response);

        paymentService.makePayment(request);

        assertEquals(
                BookingStatus.CONFIRMED,
                reservation.getBookingStatus());

        verify(reservationRepository)
                .save(reservation);
    }

    @Test
    void getPaymentById_shouldReturnPayment() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentMapper.toResponse(payment))
                .thenReturn(response);

        PaymentResponse result =
                paymentService.getPaymentById(1L);

        assertNotNull(result);
        assertEquals(response, result);

        verify(paymentRepository)
                .findById(1L);

        verify(paymentMapper)
                .toResponse(payment);
    }

    @Test
    void getPaymentById_shouldThrowExceptionWhenNotFound() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.getPaymentById(1L)
        );

        verify(paymentMapper, never())
                .toResponse(any());
    }
}