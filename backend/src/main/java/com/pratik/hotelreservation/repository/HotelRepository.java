package com.pratik.hotelreservation.repository;

import com.pratik.hotelreservation.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

}