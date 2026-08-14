package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.request.ReservationCreateRequest;
import com.pratik.hotelreservation.dto.response.ReservationResponse;
import com.pratik.hotelreservation.entity.Reservation;
import com.pratik.hotelreservation.entity.Room;
import com.pratik.hotelreservation.entity.User;
import com.pratik.hotelreservation.enums.BookingStatus;
import com.pratik.hotelreservation.exception.BusinessException;
import com.pratik.hotelreservation.exception.ResourceNotFoundException;
import com.pratik.hotelreservation.mapper.ReservationMapper;
import com.pratik.hotelreservation.repository.ReservationRepository;
import com.pratik.hotelreservation.repository.RoomRepository;
import com.pratik.hotelreservation.repository.UserRepository;
import com.pratik.hotelreservation.service.DistributedLockService;
import com.pratik.hotelreservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ReservationMapper reservationMapper;
    private final DistributedLockService distributedLockService;

    @Override
    @Transactional
    public ReservationResponse createReservation(
            ReservationCreateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        String lockKey = "room-lock:" + room.getId();

        if (!distributedLockService.tryLock(lockKey)) {
            throw new BusinessException(
                    "Room is currently being booked by another user. Please try again."
            );
        }

        try {

            log.info(
                    "Processing reservation for room ID: {}",
                    room.getId()
            );

            if (!room.getAvailable()) {
                throw new BusinessException(
                        "Room is currently unavailable");
            }

            if (request.getNumberOfGuests() > room.getCapacity()) {
                throw new BusinessException(
                        "Room capacity exceeded");
            }

            if (!request.getCheckOutDate()
                    .isAfter(request.getCheckInDate())) {

                throw new BusinessException(
                        "Check-out date must be after check-in date");
            }

            boolean roomBooked = !reservationRepository
                    .findByRoomIdAndCheckOutDateGreaterThanEqualAndCheckInDateLessThanEqual(
                            room.getId(),
                            request.getCheckInDate(),
                            request.getCheckOutDate())
                    .isEmpty();

            if (roomBooked) {
                throw new BusinessException(
                        "Room is already booked for the selected dates");
            }

            long nights = ChronoUnit.DAYS.between(
                    request.getCheckInDate(),
                    request.getCheckOutDate());

            BigDecimal totalPrice = room.getPricePerNight()
                    .multiply(BigDecimal.valueOf(nights));

            Reservation reservation =
                    reservationMapper.toEntity(request);

            reservation.setUser(user);
            reservation.setRoom(room);
            reservation.setBookingStatus(
                    BookingStatus.CONFIRMED);
            reservation.setTotalPrice(totalPrice);

            Reservation savedReservation =
                    reservationRepository.save(reservation);

            log.info(
                    "Reservation created successfully for room ID: {}",
                    room.getId()
            );

            return reservationMapper.toResponse(savedReservation);

        } finally {

            distributedLockService.unlock(lockKey);

            log.info(
                    "Reservation lock released for room ID: {}",
                    room.getId()
            );
        }
    }

    @Override
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation = reservationRepository.findById(id)
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
    public List<ReservationResponse> getReservationsByUser(
            Long userId) {

        return reservationRepository.findByUserId(userId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> getReservationsByRoom(
            Long roomId) {

        return reservationRepository.findByRoomId(roomId)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(
                reservationId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Reservation not found"));

        if (reservation.getBookingStatus()
                == BookingStatus.CHECKED_OUT) {

            throw new BusinessException(
                    "Completed reservations cannot be cancelled");
        }

        reservation.setBookingStatus(
                BookingStatus.CANCELLED);

        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void checkIn(Long reservationId) {

        Reservation reservation = reservationRepository.findById(
                reservationId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Reservation not found"));

        if (reservation.getBookingStatus()
                != BookingStatus.CONFIRMED) {

            throw new BusinessException(
                    "Only confirmed reservations can be checked in");
        }

        reservation.setBookingStatus(
                BookingStatus.CHECKED_IN);

        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void checkOut(Long reservationId) {

        Reservation reservation = reservationRepository.findById(
                reservationId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Reservation not found"));

        if (reservation.getBookingStatus()
                != BookingStatus.CHECKED_IN) {

            throw new BusinessException(
                    "Only checked-in reservations can be checked out");
        }

        reservation.setBookingStatus(
                BookingStatus.CHECKED_OUT);

        reservationRepository.save(reservation);
    }
}