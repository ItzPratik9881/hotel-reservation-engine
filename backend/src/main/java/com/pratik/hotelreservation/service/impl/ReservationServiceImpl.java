package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.request.ReservationCreateRequest;
import com.pratik.hotelreservation.dto.response.ReservationResponse;
import com.pratik.hotelreservation.entity.Reservation;
import com.pratik.hotelreservation.entity.Room;
import com.pratik.hotelreservation.entity.User;
import com.pratik.hotelreservation.enums.BookingStatus;
import com.pratik.hotelreservation.exception.ResourceNotFoundException;
import com.pratik.hotelreservation.mapper.ReservationMapper;
import com.pratik.hotelreservation.repository.ReservationRepository;
import com.pratik.hotelreservation.repository.RoomRepository;
import com.pratik.hotelreservation.repository.UserRepository;
import com.pratik.hotelreservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationResponse createReservation(
            ReservationCreateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        if (!room.getAvailable()) {
            throw new IllegalArgumentException(
                    "Room is currently unavailable");
        }

        if (request.getNumberOfGuests() > room.getCapacity()) {
            throw new IllegalArgumentException(
                    "Room capacity exceeded");
        }

        if (!request.getCheckOutDate()
                .isAfter(request.getCheckInDate())) {

            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date");
        }

        boolean roomBooked =
                !reservationRepository
                        .findByRoomIdAndCheckOutDateGreaterThanEqualAndCheckInDateLessThanEqual(
                                room.getId(),
                                request.getCheckInDate(),
                                request.getCheckOutDate()
                        )
                        .isEmpty();

        if (roomBooked) {
            throw new IllegalArgumentException(
                    "Room is already booked for the selected dates");
        }

        long nights =
                ChronoUnit.DAYS.between(
                        request.getCheckInDate(),
                        request.getCheckOutDate()
                );

        BigDecimal totalPrice =
                room.getPricePerNight()
                        .multiply(BigDecimal.valueOf(nights));

        Reservation reservation =
                reservationMapper.toEntity(request);

        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setBookingStatus(
                BookingStatus.CONFIRMED);
        reservation.setTotalPrice(totalPrice);

        Reservation saved =
                reservationRepository.save(reservation);

        return reservationMapper.toResponse(saved);
    }

    @Override
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reservation not found"));

        return reservationMapper.toResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {

        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> getReservationsByUser(Long userId) {

        return reservationRepository.findByUserId(userId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> getReservationsByRoom(Long roomId) {

        return reservationRepository.findByRoomId(roomId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public void cancelReservation(Long reservationId) {

        Reservation reservation =
                reservationRepository.findById(reservationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reservation not found"));

        reservation.setBookingStatus(
                BookingStatus.CANCELLED);

        reservationRepository.save(reservation);
    }
}