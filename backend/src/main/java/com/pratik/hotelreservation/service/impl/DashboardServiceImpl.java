package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.response.DashboardResponse;
import com.pratik.hotelreservation.enums.BookingStatus;
import com.pratik.hotelreservation.enums.PaymentStatus;
import com.pratik.hotelreservation.repository.DashboardRepository;
import com.pratik.hotelreservation.repository.HotelRepository;
import com.pratik.hotelreservation.repository.RoomRepository;
import com.pratik.hotelreservation.repository.UserRepository;
import com.pratik.hotelreservation.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardResponse getDashboard() {

        long totalHotels = hotelRepository.count();

        long totalRooms = roomRepository.count();

        long totalUsers = userRepository.count();

        long totalReservations =
                dashboardRepository.countTotalReservations();

        long confirmedReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CONFIRMED);

        long cancelledReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CANCELLED);

        long checkedInReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CHECKED_IN);

        long checkedOutReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CHECKED_OUT);

        BigDecimal totalRevenue =
                dashboardRepository.calculateTotalRevenue(
                        PaymentStatus.SUCCESS);

        long availableRooms =
                roomRepository.countByAvailableTrue();

        long occupiedRooms =
                roomRepository.countByAvailableFalse();

        return DashboardResponse.builder()
                .totalHotels(totalHotels)
                .totalRooms(totalRooms)
                .totalUsers(totalUsers)
                .totalReservations(totalReservations)
                .confirmedReservations(confirmedReservations)
                .cancelledReservations(cancelledReservations)
                .checkedInReservations(checkedInReservations)
                .checkedOutReservations(checkedOutReservations)
                .totalRevenue(totalRevenue)
                .availableRooms(availableRooms)
                .occupiedRooms(occupiedRooms)
                .build();
    }
}