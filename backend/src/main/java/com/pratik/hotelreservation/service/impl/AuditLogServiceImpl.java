package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    @Override
    public void logReservationCreated(
            Long reservationId,
            Long userId,
            Long roomId) {

        log.info(
                "AUDIT | RESERVATION_CREATED | reservationId={} | userId={} | roomId={}",
                reservationId,
                userId,
                roomId
        );
    }

    @Override
    public void logReservationCancelled(Long reservationId) {

        log.info(
                "AUDIT | RESERVATION_CANCELLED | reservationId={}",
                reservationId
        );
    }

    @Override
    public void logGuestCheckedIn(Long reservationId) {

        log.info(
                "AUDIT | GUEST_CHECKED_IN | reservationId={}",
                reservationId
        );
    }

    @Override
    public void logGuestCheckedOut(Long reservationId) {

        log.info(
                "AUDIT | GUEST_CHECKED_OUT | reservationId={}",
                reservationId
        );
    }
}