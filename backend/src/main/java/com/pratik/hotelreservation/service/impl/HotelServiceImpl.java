package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.request.HotelCreateRequest;
import com.pratik.hotelreservation.dto.request.HotelUpdateRequest;
import com.pratik.hotelreservation.dto.response.HotelResponse;
import com.pratik.hotelreservation.entity.Hotel;
import com.pratik.hotelreservation.exception.ResourceNotFoundException;
import com.pratik.hotelreservation.mapper.HotelMapper;
import com.pratik.hotelreservation.repository.HotelRepository;
import com.pratik.hotelreservation.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    @Override
    public HotelResponse createHotel(HotelCreateRequest request) {

        log.info("Creating hotel: {}", request.getName());

        Hotel hotel = hotelMapper.toEntity(request);
        hotel.setActive(true);

        Hotel savedHotel = hotelRepository.save(hotel);

        log.info("Hotel created successfully with id: {}", savedHotel.getId());

        return hotelMapper.toResponse(savedHotel);
    }

    @Override
    public HotelResponse updateHotel(Long id, HotelUpdateRequest request) {

        log.info("Updating hotel with id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Hotel not found with id: {}", id);
                    return new ResourceNotFoundException("Hotel not found");
                });

        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());
        hotel.setCity(request.getCity());
        hotel.setState(request.getState());
        hotel.setCountry(request.getCountry());
        hotel.setStarRating(request.getStarRating());
        hotel.setDescription(request.getDescription());
        hotel.setActive(request.getActive());

        Hotel updatedHotel = hotelRepository.save(hotel);

        log.info("Hotel updated successfully with id: {}", id);

        return hotelMapper.toResponse(updatedHotel);
    }

    @Override
    public HotelResponse getHotelById(Long id) {

        log.info("Fetching hotel with id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Hotel not found with id: {}", id);
                    return new ResourceNotFoundException("Hotel not found");
                });

        log.info("Hotel fetched successfully with id: {}", id);

        return hotelMapper.toResponse(hotel);
    }

    @Override
    public List<HotelResponse> getAllHotels() {

        log.info("Fetching all hotels");

        List<HotelResponse> hotels = hotelRepository.findAll()
                .stream()
                .map(hotelMapper::toResponse)
                .toList();

        log.info("Successfully fetched {} hotels", hotels.size());

        return hotels;
    }

    @Override
    public void deleteHotel(Long id) {

        log.info("Deleting hotel with id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Hotel not found with id: {}", id);
                    return new ResourceNotFoundException("Hotel not found");
                });

        hotelRepository.delete(hotel);

        log.info("Hotel deleted successfully with id: {}", id);
    }
}