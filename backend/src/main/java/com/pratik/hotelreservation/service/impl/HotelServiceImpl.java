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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    @Override
    @CacheEvict(value = "hotels", allEntries = true)
    public HotelResponse createHotel(HotelCreateRequest request) {

        Hotel hotel = hotelMapper.toEntity(request);

        hotel.setActive(true);

        Hotel savedHotel = hotelRepository.save(hotel);

        return hotelMapper.toResponse(savedHotel);
    }

    @Override
    @CachePut(value = "hotels", key = "#id")
    public HotelResponse updateHotel(
            Long id,
            HotelUpdateRequest request) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found"));

        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());
        hotel.setCity(request.getCity());
        hotel.setState(request.getState());
        hotel.setCountry(request.getCountry());
        hotel.setStarRating(request.getStarRating());
        hotel.setDescription(request.getDescription());
        hotel.setActive(request.getActive());

        Hotel updatedHotel =
                hotelRepository.save(hotel);

        return hotelMapper.toResponse(updatedHotel);
    }

    @Override
    @Cacheable(value = "hotels", key = "#id")
    public HotelResponse getHotelById(Long id) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found"));

        return hotelMapper.toResponse(hotel);
    }

    @Override
    public List<HotelResponse> getAllHotels() {

        return hotelRepository.findAll()
                .stream()
                .map(hotelMapper::toResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "hotels", key = "#id")
    public void deleteHotel(Long id) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found"));

        hotelRepository.delete(hotel);
    }
}