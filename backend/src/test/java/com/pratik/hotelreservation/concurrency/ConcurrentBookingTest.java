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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
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

    private Long testRoomId;
    private final List<Long> testUserIds = new ArrayList<>();

    @BeforeEach
    void setUp() {

        // Create a dedicated test hotel
        Hotel hotel = Hotel.builder()
                .name("CI Test Hotel")
                .address("Test Address")
                .city("Pune")
                .state("Maharashtra")
                .country("India")
                .starRating(5)
                .description("Hotel used for concurrency testing")
                .active(true)
                .build();

        hotel = hotelRepository.save(hotel);

        // Create a dedicated test room
        Room room = Room.builder()
                .roomNumber("CI-ROOM-001")
                .roomType(RoomType.STANDARD)
                .capacity(2)
                .pricePerNight(new BigDecimal("2500.00"))
                .available(true)
                .hotel(hotel)
                .build();

        room = roomRepository.save(room);

        testRoomId = room.getId();

        // Create 10 dedicated test users
        for (int i = 1; i <= 10; i++) {

            User user = User.builder()
                    .firstName("CI")
                    .lastName("User" + i)
                    .email("ci-test-user-" + i + "@example.com")
                    .password("test-password")
                    .phoneNumber("900000000" + i)
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

        for (Long userId : testUserIds) {

            futures.add(
                    executor.submit(() -> {

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

                            return false;
                        }
                    })
            );
        }

        // Release all threads at approximately the same time
        startLatch.countDown();

        int successfulBookings = 0;

        for (Future<Boolean> future : futures) {

            try {

                if (future.get(30, TimeUnit.SECONDS)) {
                    successfulBookings++;
                }

            } catch (ExecutionException |
                     TimeoutException ex) {

                // Expected for failed concurrent booking attempts
            }
        }

        executor.shutdown();
        executor.awaitTermination(
                30,
                TimeUnit.SECONDS
        );

        assertEquals(
                1,
                successfulBookings,
                "Exactly one booking should succeed"
        );

        long reservationCount =
                reservationRepository
                        .findByRoomId(testRoomId)
                        .stream()
                        .filter(reservation ->
                                reservation.getCheckInDate()
                                        .equals(LocalDate.of(2030, 1, 10)))
                        .count();

        assertEquals(
                1,
                reservationCount,
                "Exactly one reservation should exist for the room"
        );
    }
}