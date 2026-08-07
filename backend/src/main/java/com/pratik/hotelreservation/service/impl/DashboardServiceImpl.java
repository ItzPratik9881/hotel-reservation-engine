package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.response.BookingAnalyticsResponse;
import com.pratik.hotelreservation.dto.response.DashboardResponse;
import com.pratik.hotelreservation.dto.response.RevenueResponse;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
                        BookingStatus.CONFIRMED
                );

        long cancelledReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CANCELLED
                );

        long checkedInReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CHECKED_IN
                );

        long checkedOutReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CHECKED_OUT
                );

        BigDecimal totalRevenue =
                dashboardRepository.calculateTotalRevenue(
                        PaymentStatus.SUCCESS
                );

        long availableRooms =
                roomRepository.countByAvailableTrue();

        long occupiedRooms =
                roomRepository.countByAvailableFalse();

        BigDecimal occupancyRate = BigDecimal.ZERO;

        if (totalRooms > 0) {

            occupancyRate = BigDecimal.valueOf(occupiedRooms)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            BigDecimal.valueOf(totalRooms),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

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
                .occupancyRate(occupancyRate)
                .build();
    }

    @Override
    public RevenueResponse getRevenue() {

        LocalDate today = LocalDate.now();

        LocalDateTime todayStart =
                today.atStartOfDay();

        LocalDateTime tomorrowStart =
                today.plusDays(1).atStartOfDay();

        LocalDate firstDayOfMonth =
                today.withDayOfMonth(1);

        LocalDateTime monthStart =
                firstDayOfMonth.atStartOfDay();

        LocalDateTime nextMonthStart =
                firstDayOfMonth
                        .plusMonths(1)
                        .atStartOfDay();

        LocalDate firstDayOfYear =
                today.withDayOfYear(1);

        LocalDateTime yearStart =
                firstDayOfYear.atStartOfDay();

        LocalDateTime nextYearStart =
                firstDayOfYear
                        .plusYears(1)
                        .atStartOfDay();

        BigDecimal todayRevenue =
                dashboardRepository.calculateRevenueBetween(
                        PaymentStatus.SUCCESS,
                        todayStart,
                        tomorrowStart
                );

        BigDecimal monthlyRevenue =
                dashboardRepository.calculateRevenueBetween(
                        PaymentStatus.SUCCESS,
                        monthStart,
                        nextMonthStart
                );

        BigDecimal yearlyRevenue =
                dashboardRepository.calculateRevenueBetween(
                        PaymentStatus.SUCCESS,
                        yearStart,
                        nextYearStart
                );

        return RevenueResponse.builder()
                .todayRevenue(todayRevenue)
                .monthlyRevenue(monthlyRevenue)
                .yearlyRevenue(yearlyRevenue)
                .build();
    }

    @Override
    public BookingAnalyticsResponse getBookingAnalytics() {

        long totalReservations =
                dashboardRepository.countTotalReservations();

        long confirmedReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CONFIRMED
                );

        long cancelledReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CANCELLED
                );

        long checkedInReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CHECKED_IN
                );

        long checkedOutReservations =
                dashboardRepository.countReservationsByStatus(
                        BookingStatus.CHECKED_OUT
                );

        return BookingAnalyticsResponse.builder()
                .totalReservations(totalReservations)
                .confirmedReservations(confirmedReservations)
                .cancelledReservations(cancelledReservations)
                .checkedInReservations(checkedInReservations)
                .checkedOutReservations(checkedOutReservations)
                .build();
    }
}