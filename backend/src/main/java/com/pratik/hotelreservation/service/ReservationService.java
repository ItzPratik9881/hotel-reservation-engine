package com.pratik.hotelreservation.service;

import com.pratik.hotelreservation.dto.request.ReservationCreateRequest;
import com.pratik.hotelreservation.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(
            ReservationCreateRequest request);

    ReservationResponse getReservationById(Long id);

    List<ReservationResponse> getAllReservations();

    List<ReservationResponse> getReservationsByUser(Long userId);

    List<ReservationResponse> getReservationsByRoom(Long roomId);

    void cancelReservation(Long reservationId);

    void checkIn(Long reservationId);

    void checkOut(Long reservationId);
}