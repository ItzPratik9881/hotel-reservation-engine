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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private DistributedLockService distributedLockService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private ReservationCreateRequest request;
    private User user;
    private Room room;
    private Reservation reservation;
    private ReservationResponse response;

    @BeforeEach
    void setUp() {

        request = new ReservationCreateRequest();

        request.setUserId(1L);
        request.setRoomId(1L);
        request.setCheckInDate(
                LocalDate.of(2026, 9, 10));
        request.setCheckOutDate(
                LocalDate.of(2026, 9, 13));
        request.setNumberOfGuests(2);

        user = new User();
        user.setId(1L);

        room = new Room();
        room.setId(1L);
        room.setAvailable(true);
        room.setCapacity(3);
        room.setPricePerNight(
                new BigDecimal("2000.00"));

        reservation = new Reservation();

        response = new ReservationResponse();
    }

    @Test
    void createReservation_shouldCreateSuccessfully() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(distributedLockService.tryLock("room-lock:1"))
                .thenReturn(true);

        when(reservationRepository
                .existsByRoomIdAndCheckOutDateGreaterThanEqualAndCheckInDateLessThanEqual(
                        eq(1L),
                        eq(request.getCheckInDate()),
                        eq(request.getCheckOutDate())))
                .thenReturn(false);

        when(reservationMapper.toEntity(request))
                .thenReturn(reservation);

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);

        when(reservationMapper.toResponse(reservation))
                .thenReturn(response);

        ReservationResponse result =
                reservationService.createReservation(request);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(user, reservation.getUser());
        assertEquals(room, reservation.getRoom());
        assertEquals(
                BookingStatus.CONFIRMED,
                reservation.getBookingStatus());

        assertEquals(
                new BigDecimal("6000.00"),
                reservation.getTotalPrice());

        verify(reservationRepository)
                .save(reservation);

        verify(distributedLockService)
                .tryLock("room-lock:1");

        verify(distributedLockService)
                .unlock("room-lock:1");
    }

    @Test
    void createReservation_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request)
        );

        verify(roomRepository, never())
                .findById(anyLong());

        verify(distributedLockService, never())
                .tryLock(anyString());
    }

    @Test
    void createReservation_shouldThrowExceptionWhenRoomNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request)
        );

        verify(distributedLockService, never())
                .tryLock(anyString());
    }

    @Test
    void createReservation_shouldThrowExceptionWhenLockCannotBeAcquired() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(distributedLockService.tryLock("room-lock:1"))
                .thenReturn(false);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> reservationService.createReservation(request)
                );

        assertEquals(
                "Room is currently being booked by another user. Please try again.",
                exception.getMessage());

        verify(reservationRepository, never())
                .save(any());

        verify(distributedLockService, never())
                .unlock(anyString());
    }

    @Test
    void createReservation_shouldThrowExceptionWhenRoomUnavailable() {

        room.setAvailable(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(distributedLockService.tryLock("room-lock:1"))
                .thenReturn(true);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> reservationService.createReservation(request)
                );

        assertEquals(
                "Room is currently unavailable",
                exception.getMessage());

        verify(reservationRepository, never())
                .save(any());

        verify(distributedLockService)
                .unlock("room-lock:1");
    }

    @Test
    void createReservation_shouldThrowExceptionWhenCapacityExceeded() {

        request.setNumberOfGuests(4);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(distributedLockService.tryLock("room-lock:1"))
                .thenReturn(true);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> reservationService.createReservation(request)
                );

        assertEquals(
                "Room capacity exceeded",
                exception.getMessage());

        verify(reservationRepository, never())
                .save(any());

        verify(distributedLockService)
                .unlock("room-lock:1");
    }

    @Test
    void createReservation_shouldThrowExceptionWhenDatesAreInvalid() {

        request.setCheckInDate(
                LocalDate.of(2026, 9, 15));

        request.setCheckOutDate(
                LocalDate.of(2026, 9, 12));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(distributedLockService.tryLock("room-lock:1"))
                .thenReturn(true);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> reservationService.createReservation(request)
                );

        assertEquals(
                "Check-out date must be after check-in date",
                exception.getMessage());

        verify(reservationRepository, never())
                .save(any());

        verify(distributedLockService)
                .unlock("room-lock:1");
    }

    @Test
    void createReservation_shouldThrowExceptionWhenRoomAlreadyBooked() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(distributedLockService.tryLock("room-lock:1"))
                .thenReturn(true);

        when(reservationRepository
                .existsByRoomIdAndCheckOutDateGreaterThanEqualAndCheckInDateLessThanEqual(
                        eq(1L),
                        eq(request.getCheckInDate()),
                        eq(request.getCheckOutDate())))
                .thenReturn(true);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> reservationService.createReservation(request)
                );

        assertEquals(
                "Room is already booked for the selected dates",
                exception.getMessage());

        verify(reservationRepository, never())
                .save(any());

        verify(distributedLockService)
                .unlock("room-lock:1");
    }
}