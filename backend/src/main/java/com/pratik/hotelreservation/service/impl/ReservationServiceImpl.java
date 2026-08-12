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
import com.pratik.hotelreservation.service.AuditLogService;
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
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ReservationResponse createReservation(
            ReservationCreateRequest request) {

        log.info(
                "Creating reservation for userId: {}, roomId: {}",
                request.getUserId(),
                request.getRoomId()
        );

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn(
                            "User not found with id: {}",
                            request.getUserId()
                    );
                    return new ResourceNotFoundException("User not found");
                });

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> {
                    log.warn(
                            "Room not found with id: {}",
                            request.getRoomId()
                    );
                    return new ResourceNotFoundException("Room not found");
                });

        if (!room.getAvailable()) {

            log.warn(
                    "Reservation rejected. Room unavailable: {}",
                    room.getId()
            );

            throw new BusinessException(
                    "Room is currently unavailable"
            );
        }

        if (request.getNumberOfGuests() > room.getCapacity()) {

            log.warn(
                    "Reservation rejected. Capacity exceeded for room: {}",
                    room.getId()
            );

            throw new BusinessException(
                    "Room capacity exceeded"
            );
        }

        if (!request.getCheckOutDate()
                .isAfter(request.getCheckInDate())) {

            log.warn(
                    "Reservation rejected. Invalid dates for room: {}",
                    room.getId()
            );

            throw new BusinessException(
                    "Check-out date must be after check-in date"
            );
        }

        boolean roomBooked = !reservationRepository
                .findByRoomIdAndCheckOutDateGreaterThanEqualAndCheckInDateLessThanEqual(
                        room.getId(),
                        request.getCheckInDate(),
                        request.getCheckOutDate()
                )
                .isEmpty();

        if (roomBooked) {

            log.warn(
                    "Reservation rejected. Room {} already booked for selected dates",
                    room.getId()
            );

            throw new BusinessException(
                    "Room is already booked for the selected dates"
            );
        }

        long nights = ChronoUnit.DAYS.between(
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        BigDecimal totalPrice = room.getPricePerNight()
                .multiply(BigDecimal.valueOf(nights));

        Reservation reservation =
                reservationMapper.toEntity(request);

        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setBookingStatus(
                BookingStatus.CONFIRMED
        );
        reservation.setTotalPrice(totalPrice);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        log.info(
                "Reservation created successfully with id: {}",
                savedReservation.getId()
        );

        auditLogService.logReservationCreated(
                savedReservation.getId(),
                request.getUserId(),
                request.getRoomId()
        );

        return reservationMapper.toResponse(savedReservation);
    }

    @Override
    public ReservationResponse getReservationById(Long id) {

        log.info(
                "Fetching reservation with id: {}",
                id
        );

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "Reservation not found with id: {}",
                            id
                    );
                    return new ResourceNotFoundException(
                            "Reservation not found"
                    );
                });

        log.info(
                "Reservation fetched successfully with id: {}",
                id
        );

        return reservationMapper.toResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {

        log.info("Fetching all reservations");

        List<ReservationResponse> reservations =
                reservationRepository.findAll()
                        .stream()
                        .map(reservationMapper::toResponse)
                        .toList();

        log.info(
                "Successfully fetched {} reservations",
                reservations.size()
        );

        return reservations;
    }

    @Override
    public List<ReservationResponse> getReservationsByUser(
            Long userId) {

        log.info(
                "Fetching reservations for userId: {}",
                userId
        );

        List<ReservationResponse> reservations =
                reservationRepository.findByUserId(userId)
                        .stream()
                        .map(reservationMapper::toResponse)
                        .toList();

        log.info(
                "Found {} reservations for userId: {}",
                reservations.size(),
                userId
        );

        return reservations;
    }

    @Override
    public List<ReservationResponse> getReservationsByRoom(
            Long roomId) {

        log.info(
                "Fetching reservations for roomId: {}",
                roomId
        );

        List<ReservationResponse> reservations =
                reservationRepository.findByRoomId(roomId)
                        .stream()
                        .map(reservationMapper::toResponse)
                        .toList();

        log.info(
                "Found {} reservations for roomId: {}",
                reservations.size(),
                roomId
        );

        return reservations;
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {

        log.info(
                "Cancelling reservation with id: {}",
                reservationId
        );

        Reservation reservation =
                reservationRepository.findById(reservationId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Reservation not found with id: {}",
                                    reservationId
                            );

                            return new ResourceNotFoundException(
                                    "Reservation not found"
                            );
                        });

        if (reservation.getBookingStatus()
                == BookingStatus.CHECKED_OUT) {

            log.warn(
                    "Cancellation rejected. Reservation already checked out: {}",
                    reservationId
            );

            throw new BusinessException(
                    "Completed reservations cannot be cancelled"
            );
        }

        reservation.setBookingStatus(
                BookingStatus.CANCELLED
        );

        reservationRepository.save(reservation);

        auditLogService.logReservationCancelled(
                reservationId
        );

        log.info(
                "Reservation cancelled successfully with id: {}",
                reservationId
        );
    }

    @Override
    @Transactional
    public void checkIn(Long reservationId) {

        log.info(
                "Checking in reservation with id: {}",
                reservationId
        );

        Reservation reservation =
                reservationRepository.findById(reservationId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Reservation not found with id: {}",
                                    reservationId
                            );

                            return new ResourceNotFoundException(
                                    "Reservation not found"
                            );
                        });

        if (reservation.getBookingStatus()
                != BookingStatus.CONFIRMED) {

            log.warn(
                    "Check-in rejected. Reservation {} has status: {}",
                    reservationId,
                    reservation.getBookingStatus()
            );

            throw new BusinessException(
                    "Only confirmed reservations can be checked in"
            );
        }

        reservation.setBookingStatus(
                BookingStatus.CHECKED_IN
        );

        reservationRepository.save(reservation);

        auditLogService.logGuestCheckedIn(
                reservationId
        );

        log.info(
                "Guest checked in successfully for reservation: {}",
                reservationId
        );
    }

    @Override
    @Transactional
    public void checkOut(Long reservationId) {

        log.info(
                "Checking out reservation with id: {}",
                reservationId
        );

        Reservation reservation =
                reservationRepository.findById(reservationId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Reservation not found with id: {}",
                                    reservationId
                            );

                            return new ResourceNotFoundException(
                                    "Reservation not found"
                            );
                        });

        if (reservation.getBookingStatus()
                != BookingStatus.CHECKED_IN) {

            log.warn(
                    "Check-out rejected. Reservation {} has status: {}",
                    reservationId,
                    reservation.getBookingStatus()
            );

            throw new BusinessException(
                    "Only checked-in reservations can be checked out"
            );
        }

        reservation.setBookingStatus(
                BookingStatus.CHECKED_OUT
        );

        reservationRepository.save(reservation);

        auditLogService.logGuestCheckedOut(
                reservationId
        );

        log.info(
                "Guest checked out successfully for reservation: {}",
                reservationId
        );
    }
}