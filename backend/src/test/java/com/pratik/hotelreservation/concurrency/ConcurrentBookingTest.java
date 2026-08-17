package com.pratik.hotelreservation.concurrency;

import com.pratik.hotelreservation.dto.request.ReservationCreateRequest;
import com.pratik.hotelreservation.dto.response.ReservationResponse;
import com.pratik.hotelreservation.repository.ReservationRepository;
import com.pratik.hotelreservation.service.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrentBookingTest {

    private static final Long TEST_ROOM_ID = 4L;

    private final List<Long> createdReservationIds =
            new ArrayList<>();

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void shouldAllowOnlyOneBookingForSameRoom() throws Exception {

        int numberOfUsers = 10;

        LocalDate checkInDate =
                LocalDate.now().plusDays(30);

        LocalDate checkOutDate =
                checkInDate.plusDays(2);

        ExecutorService executorService =
                Executors.newFixedThreadPool(numberOfUsers);

        CountDownLatch startSignal =
                new CountDownLatch(1);

        List<Future<Result>> results =
                new ArrayList<>();

        for (long userId = 1L;
             userId <= numberOfUsers;
             userId++) {

            long currentUserId = userId;

            results.add(
                    executorService.submit(() -> {

                        startSignal.await();

                        ReservationCreateRequest request =
                                ReservationCreateRequest.builder()
                                        .userId(currentUserId)
                                        .roomId(TEST_ROOM_ID)
                                        .checkInDate(checkInDate)
                                        .checkOutDate(checkOutDate)
                                        .numberOfGuests(1)
                                        .build();

                        try {

                            ReservationResponse response =
                                    reservationService
                                            .createReservation(request);

                            System.out.println(
                                    "Booking SUCCESS for user: "
                                            + currentUserId
                            );

                            return Result.success(response);

                        } catch (Exception e) {

                            System.out.println(
                                    "Booking FAILED for user: "
                                            + currentUserId
                                            + " | "
                                            + e.getClass()
                                            .getSimpleName()
                                            + " - "
                                            + e.getMessage()
                            );

                            return Result.failure(e);
                        }
                    })
            );
        }

        // Start all requests simultaneously
        startSignal.countDown();

        int successfulBookings = 0;
        int failedBookings = 0;

        for (Future<Result> result : results) {

            Result bookingResult = result.get();

            if (bookingResult.success) {

                successfulBookings++;

                if (bookingResult.response != null) {

                    createdReservationIds.add(
                            bookingResult.response.getId()
                    );
                }

            } else {

                failedBookings++;
            }
        }

        executorService.shutdown();

        System.out.println(
                "===================================="
        );

        System.out.println(
                "Successful bookings: "
                        + successfulBookings
        );

        System.out.println(
                "Failed bookings: "
                        + failedBookings
        );

        System.out.println(
                "===================================="
        );

        // ---------------------------------------------------------
        // Verify concurrent request results
        // ---------------------------------------------------------

        assertEquals(
                1,
                successfulBookings,
                "Exactly one booking should succeed"
        );

        assertEquals(
                9,
                failedBookings,
                "Exactly nine bookings should fail"
        );

        // ---------------------------------------------------------
        // Verify database consistency
        // ---------------------------------------------------------

        long databaseReservationCount =
                reservationRepository
                        .findByRoomId(TEST_ROOM_ID)
                        .stream()
                        .filter(reservation ->
                                reservation.getCheckInDate()
                                        .equals(checkInDate))
                        .filter(reservation ->
                                reservation.getCheckOutDate()
                                        .equals(checkOutDate))
                        .count();

        System.out.println(
                "Reservations found in database: "
                        + databaseReservationCount
        );

        assertEquals(
                1,
                databaseReservationCount,
                "Database must contain exactly one reservation"
        );
    }

    @AfterEach
    void cleanup() {

        for (Long reservationId :
                createdReservationIds) {

            reservationRepository
                    .findById(reservationId)
                    .ifPresent(reservation ->
                            reservationRepository
                                    .delete(reservation)
                    );
        }

        createdReservationIds.clear();

        System.out.println(
                "Test reservations cleaned up."
        );
    }

    private static class Result {

        private final boolean success;
        private final ReservationResponse response;
        private final Exception exception;

        private Result(
                boolean success,
                ReservationResponse response,
                Exception exception) {

            this.success = success;
            this.response = response;
            this.exception = exception;
        }

        static Result success(
                ReservationResponse response) {

            return new Result(
                    true,
                    response,
                    null
            );
        }

        static Result failure(
                Exception exception) {

            return new Result(
                    false,
                    null,
                    exception
            );
        }
    }
}