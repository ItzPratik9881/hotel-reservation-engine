package com.pratik.hotelreservation.repository;

import com.pratik.hotelreservation.entity.Reservation;
import com.pratik.hotelreservation.enums.BookingStatus;
import com.pratik.hotelreservation.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface DashboardRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT COUNT(r) FROM Reservation r")
    long countTotalReservations();

    @Query("""
            SELECT COUNT(r)
            FROM Reservation r
            WHERE r.bookingStatus = :status
            """)
    long countReservationsByStatus(
            @Param("status") BookingStatus status
    );

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.paymentStatus = :status
            """)
    BigDecimal calculateTotalRevenue(
            @Param("status") PaymentStatus status
    );
}