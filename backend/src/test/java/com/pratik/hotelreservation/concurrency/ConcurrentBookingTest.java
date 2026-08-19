package com.pratik.hotelreservation.concurrency;

import com.pratik.hotelreservation.dto.request.ReservationCreateRequest;
import com.pratik.hotelreservation.dto.response.ReservationResponse;
import com.pratik.hotelreservation.entity.Hotel;
import com.pratik.hotelreservation.entity.Room;
import com.pratik.hotelreservation.entity.User;
import com.pratik.hotelreservation.enums.Role;
import com.pratik.hotelreservation.enums.RoomType;
import com.pratik.hotelreservation.repository.HotelRepository;
import com.pratik.hotelreservation.repository.ReservationRepository;
import com.pratik.hotelreservation.repository.RoomRepository;
import com.pratik.hotelreservation.repository.UserRepository;
import com.pratik.hotelreservation.service.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ConcurrentBookingTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Long testHotelId;
    private Long testRoomId;
    private String testRunId;

    private final List<Long> testUserIds = new ArrayList<>();

    @BeforeEach
    void setUp() {

        /*
         * Generate a unique ID for every test execution.
         *
         * This prevents duplicate email errors when the test
         * is executed multiple times against the same database.
         */
        testRunId = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        /*
         * Create dedicated test hotel.
         */
        Hotel hotel = Hotel.builder()
                .name("CI Test Hotel " + testRunId)
                .address("Test Address")
                .city("Pune")
                .state("Maharashtra")
                .country("India")
                .starRating(5)
                .description("Hotel used for concurrency testing")
                .active(true)
                .build();

        hotel = hotelRepository.save(hotel);

        testHotelId = hotel.getId();

        /*
         * Create dedicated test room.
         */
        Room room = Room.builder()
                .roomNumber("CI-ROOM-" + testRunId)
                .roomType(RoomType.STANDARD)
                .capacity(2)
                .pricePerNight(new BigDecimal("2500.00"))
                .available(true)
                .hotel(hotel)
                .build();

        room = roomRepository.save(room);

        testRoomId = room.getId();

        /*
         * Create 10 dedicated test users.
         */
        for (int i = 1; i <= 10; i++) {

            User user = User.builder()
                    .firstName("CI")
                    .lastName("User" + i)
                    .email(
                            "ci-test-"
                                    + testRunId
                                    + "-user-"
                                    + i
                                    + "@example.com"
                    )
                    .password("test-password")
                    .phoneNumber("90000000" + String.format("%02d", i))
                    .role(Role.CUSTOMER)
                    .enabled(true)
                    .build();

            User savedUser = userRepository.save(user);

            testUserIds.add(savedUser.getId());
        }
    }

    @Test
    void shouldAllowOnlyOneBookingForSameRoom()
            throws InterruptedException {

        int numberOfUsers = 10;

        ExecutorService executor =
                Executors.newFixedThreadPool(numberOfUsers);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        List<Future<Boolean>> futures =
                new ArrayList<>();

        /*
         * Create 10 concurrent booking requests.
         */
        for (Long userId : testUserIds) {

            futures.add(
                    executor.submit(() -> {

                        /*
                         * Wait until all threads are ready.
                         */
                        startLatch.await();

                        try {

                            ReservationCreateRequest request =
                                    new ReservationCreateRequest();

                            request.setUserId(userId);
                            request.setRoomId(testRoomId);

                            request.setCheckInDate(
                                    LocalDate.of(2030, 1, 10)
                            );

                            request.setCheckOutDate(
                                    LocalDate.of(2030, 1, 13)
                            );

                            request.setNumberOfGuests(2);

                            ReservationResponse response =
                                    reservationService
                                            .createReservation(request);

                            return response != null;

                        } catch (Exception ex) {

                            /*
                             * Failed concurrent bookings are expected.
                             */
                            return false;
                        }
                    })
            );
        }

        /*
         * Release all threads at approximately the same time.
         */
        startLatch.countDown();

        int successfulBookings = 0;

        for (Future<Boolean> future : futures) {

            try {

                if (future.get(30, TimeUnit.SECONDS)) {
                    successfulBookings++;
                }

            } catch (ExecutionException |
                     TimeoutException ex) {

                /*
                 * A failed booking is expected.
                 */
            }
        }

        executor.shutdown();

        executor.awaitTermination(
                30,
                TimeUnit.SECONDS
        );

        /*
         * Exactly one request should succeed.
         */
        assertEquals(
                1,
                successfulBookings,
                "Exactly one booking should succeed"
        );

        /*
         * Verify the database contains exactly one
         * reservation for this room and date range.
         */
        long reservationCount =
                reservationRepository
                        .findByRoomId(testRoomId)
                        .stream()
                        .filter(reservation ->
                                reservation.getCheckInDate()
                                        .equals(
                                                LocalDate.of(
                                                        2030,
                                                        1,
                                                        10
                                                )
                                        )
                        )
                        .count();

        assertEquals(
                1,
                reservationCount,
                "Exactly one reservation should exist for the room"
        );
    }

    @AfterEach
    void cleanup() {

        /*
         * Delete reservations first because they reference
         * users and rooms.
         */
        if (testRoomId != null) {

            reservationRepository
                    .findByRoomId(testRoomId)
                    .forEach(reservation ->
                            reservationRepository.delete(reservation)
                    );
        }

        /*
         * Delete the test room.
         */
        if (testRoomId != null) {

            roomRepository.deleteById(testRoomId);
        }

        /*
         * Delete the test hotel.
         */
        if (testHotelId != null) {

            hotelRepository.deleteById(testHotelId);
        }

        /*
         * Delete the dedicated test users.
         */
        for (Long userId : testUserIds) {

            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
            }
        }

        testUserIds.clear();

        testRoomId = null;
        testHotelId = null;
    }
}