package com.pratik.hotelreservation.repository;

import com.pratik.hotelreservation.entity.Reservation;
import com.pratik.hotelreservation.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByRoomId(Long roomId);

    List<Reservation> findByBookingStatus(BookingStatus status);

    List<Reservation> findByRoomIdAndCheckOutDateGreaterThanEqualAndCheckInDateLessThanEqual(
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );

    boolean existsByRoomIdAndCheckOutDateGreaterThanEqualAndCheckInDateLessThanEqual(
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );
}