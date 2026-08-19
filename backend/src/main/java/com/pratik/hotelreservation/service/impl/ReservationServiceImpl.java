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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

        /*
         * In the real application, @Transactional creates an active
         * transaction synchronization context.
         *
         * During unit tests, the service may be called directly,
         * without a Spring transaction. In that case there is no
         * transaction synchronization, so the lock is released
         * normally in finally.
         *
         * In the real application, the lock remains active until
         * the database transaction has completed.
         */
        boolean transactionSynchronizationActive =
                TransactionSynchronizationManager.isSynchronizationActive();

        if (transactionSynchronizationActive) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {

                        @Override
                        public void afterCompletion(int status) {

                            distributedLockService.unlock(lockKey);

                            log.info(
                                    "Reservation lock released for room ID: {}",
                                    room.getId()
                            );
                        }
                    }
            );
        }

        try {

            log.info(
                    "Processing reservation for room ID: {}",
                    room.getId()
            );

            /*
             * Check whether the room itself is available.
             */
            if (!room.getAvailable()) {
                throw new BusinessException(
                        "Room is currently unavailable"
                );
            }

            /*
             * Check guest capacity.
             */
            if (request.getNumberOfGuests() > room.getCapacity()) {
                throw new BusinessException(
                        "Room capacity exceeded"
                );
            }

            /*
             * Validate reservation dates.
             */
            if (!request.getCheckOutDate()
                    .isAfter(request.getCheckInDate())) {

                throw new BusinessException(
                        "Check-out date must be after check-in date"
                );
            }

            /*
             * Check whether another reservation already exists
             * for the requested date range.
             *
             * This check is protected by the distributed lock.
             */
            boolean roomBooked =
                    reservationRepository
                            .existsByRoomIdAndCheckOutDateGreaterThanEqualAndCheckInDateLessThanEqual(
                                    room.getId(),
                                    request.getCheckInDate(),
                                    request.getCheckOutDate()
                            );

            if (roomBooked) {
                throw new BusinessException(
                        "Room is already booked for the selected dates"
                );
            }

            /*
             * Calculate total price.
             */
            long nights = ChronoUnit.DAYS.between(
                    request.getCheckInDate(),
                    request.getCheckOutDate()
            );

            BigDecimal totalPrice =
                    room.getPricePerNight()
                            .multiply(BigDecimal.valueOf(nights));

            /*
             * Create reservation entity.
             */
            Reservation reservation =
                    reservationMapper.toEntity(request);

            reservation.setUser(user);
            reservation.setRoom(room);
            reservation.setBookingStatus(
                    BookingStatus.CONFIRMED
            );
            reservation.setTotalPrice(totalPrice);

            /*
             * Save reservation.
             */
            Reservation savedReservation =
                    reservationRepository.save(reservation);

            log.info(
                    "Reservation created successfully for room ID: {}",
                    room.getId()
            );

            return reservationMapper.toResponse(savedReservation);

        } finally {

            /*
             * Unit-test path:
             *
             * There is no active transaction synchronization,
             * so release the Redis lock immediately.
             *
             * Production path:
             *
             * The lock is NOT released here. It is released by
             * afterCompletion() after the transaction completes.
             */
            if (!transactionSynchronizationActive) {

                distributedLockService.unlock(lockKey);

                log.info(
                        "Reservation lock released for room ID: {}",
                        room.getId()
                );
            }
        }
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

        Reservation reservation =
                reservationRepository.findById(reservationId)
                        .orElseThrow(() ->
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

        Reservation reservation =
                reservationRepository.findById(reservationId)
                        .orElseThrow(() ->
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

        Reservation reservation =
                reservationRepository.findById(reservationId)
                        .orElseThrow(() ->
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