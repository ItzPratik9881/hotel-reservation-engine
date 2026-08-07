package com.pratik.hotelreservation.repository;

import com.pratik.hotelreservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelId(Long hotelId);

    List<Room> findByAvailableTrue();

    Optional<Room> findByRoomNumberAndHotelId(
            String roomNumber,
            Long hotelId
    );

    boolean existsByRoomNumberAndHotelId(
            String roomNumber,
            Long hotelId
    );

    long countByAvailableTrue();

    long countByAvailableFalse();
}