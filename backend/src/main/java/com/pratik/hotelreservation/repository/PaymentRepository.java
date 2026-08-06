package com.pratik.hotelreservation.repository;

import com.pratik.hotelreservation.entity.Payment;
import com.pratik.hotelreservation.entity.Reservation;
import com.pratik.hotelreservation.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByReservation(Reservation reservation);
    
    boolean existsByReservationAndPaymentStatus(
        Reservation reservation,
        PaymentStatus paymentStatus
    );

}