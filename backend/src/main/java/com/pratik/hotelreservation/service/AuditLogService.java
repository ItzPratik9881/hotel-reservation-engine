package com.pratik.hotelreservation.service;

public interface AuditLogService {

    void logReservationCreated(Long reservationId, Long userId, Long roomId);

    void logReservationCancelled(Long reservationId);

    void logGuestCheckedIn(Long reservationId);

    void logGuestCheckedOut(Long reservationId);
}