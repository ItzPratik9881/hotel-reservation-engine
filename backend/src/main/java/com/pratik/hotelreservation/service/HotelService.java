package com.pratik.hotelreservation.service;

import com.pratik.hotelreservation.dto.request.HotelCreateRequest;
import com.pratik.hotelreservation.dto.request.HotelUpdateRequest;
import com.pratik.hotelreservation.dto.response.HotelResponse;

import java.util.List;

public interface HotelService {

    HotelResponse createHotel(HotelCreateRequest request);

    HotelResponse updateHotel(Long id, HotelUpdateRequest request);

    HotelResponse getHotelById(Long id);

    List<HotelResponse> getAllHotels();

    void deleteHotel(Long id);
}