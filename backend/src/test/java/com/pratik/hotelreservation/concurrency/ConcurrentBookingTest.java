package com.pratik.hotelreservation.concurrency;

import com.pratik.hotelreservation.dto.request.ReservationCreateRequest;
import com.pratik.hotelreservation.service.ReservationService;
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

    @Autowired
    private ReservationService reservationService;

    @Test
    void shouldAllowOnlyOneBookingForSameRoom() throws Exception {

        int numberOfUsers = 10;

        ExecutorService executorService =
                Executors.newFixedThreadPool(numberOfUsers);

        CountDownLatch startSignal =
                new CountDownLatch(1);

        List<Future<Boolean>> results =
                new ArrayList<>();

        for (int i = 0; i < numberOfUsers; i++) {

            results.add(
                    executorService.submit(() -> {

                        startSignal.await();

                        ReservationCreateRequest request =
                                ReservationCreateRequest.builder()
                                        .userId(1L)
                                        .roomId(4L)
                                        .checkInDate(
                                                LocalDate.of(2030, 1, 1))
                                        .checkOutDate(
                                                LocalDate.of(2030, 1, 3))
                                        .numberOfGuests(1)
                                        .build();

                        try {

                            reservationService.createReservation(request);

                            return true;

                        } catch (Exception e) {

                            System.out.println(
                                    "Booking failed: "
                                            + e.getClass().getSimpleName()
                                            + " - "
                                            + e.getMessage()
                            );

                            return false;
                        }
                    })
            );
        }

        // Release all threads at approximately the same time
        startSignal.countDown();

        int successfulBookings = 0;
        int failedBookings = 0;

        for (Future<Boolean> result : results) {

            if (result.get()) {
                successfulBookings++;
            } else {
                failedBookings++;
            }
        }

        executorService.shutdown();

        System.out.println(
                "Successful bookings: " + successfulBookings
        );

        System.out.println(
                "Failed bookings: " + failedBookings
        );

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
    }
}