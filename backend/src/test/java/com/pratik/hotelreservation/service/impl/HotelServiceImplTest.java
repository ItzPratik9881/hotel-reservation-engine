package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.request.HotelCreateRequest;
import com.pratik.hotelreservation.dto.request.HotelUpdateRequest;
import com.pratik.hotelreservation.dto.response.HotelResponse;
import com.pratik.hotelreservation.entity.Hotel;
import com.pratik.hotelreservation.exception.ResourceNotFoundException;
import com.pratik.hotelreservation.mapper.HotelMapper;
import com.pratik.hotelreservation.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private HotelCreateRequest createRequest;
    private HotelUpdateRequest updateRequest;
    private Hotel hotel;
    private HotelResponse response;

    @BeforeEach
    void setUp() {

        createRequest = new HotelCreateRequest();

        createRequest.setName("Grand Palace");
        createRequest.setAddress("Main Street");
        createRequest.setCity("Pune");
        createRequest.setState("Maharashtra");
        createRequest.setCountry("India");
        createRequest.setStarRating(5);
        createRequest.setDescription("Luxury hotel");

        updateRequest = new HotelUpdateRequest();

        updateRequest.setName("Grand Palace Updated");
        updateRequest.setAddress("Updated Street");
        updateRequest.setCity("Mumbai");
        updateRequest.setState("Maharashtra");
        updateRequest.setCountry("India");
        updateRequest.setStarRating(4);
        updateRequest.setDescription("Updated description");
        updateRequest.setActive(true);

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Grand Palace");
        hotel.setAddress("Main Street");
        hotel.setCity("Pune");
        hotel.setState("Maharashtra");
        hotel.setCountry("India");
        hotel.setStarRating(5);
        hotel.setDescription("Luxury hotel");
        hotel.setActive(true);

        response = mock(HotelResponse.class);
    }

    @Test
    void createHotel_shouldCreateSuccessfully() {

        when(hotelMapper.toEntity(createRequest))
                .thenReturn(hotel);

        when(hotelRepository.save(hotel))
                .thenReturn(hotel);

        when(hotelMapper.toResponse(hotel))
                .thenReturn(response);

        HotelResponse result =
                hotelService.createHotel(createRequest);

        assertNotNull(result);
        assertEquals(response, result);

        assertTrue(hotel.getActive());

        verify(hotelMapper)
                .toEntity(createRequest);

        verify(hotelRepository)
                .save(hotel);

        verify(hotelMapper)
                .toResponse(hotel);
    }

    @Test
    void updateHotel_shouldUpdateSuccessfully() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));

        when(hotelRepository.save(hotel))
                .thenReturn(hotel);

        when(hotelMapper.toResponse(hotel))
                .thenReturn(response);

        HotelResponse result =
                hotelService.updateHotel(1L, updateRequest);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(
                "Grand Palace Updated",
                hotel.getName());

        assertEquals(
                "Updated Street",
                hotel.getAddress());

        assertEquals(
                "Mumbai",
                hotel.getCity());

        assertEquals(
                4,
                hotel.getStarRating());

        assertEquals(
                "Updated description",
                hotel.getDescription());

        assertTrue(hotel.getActive());

        verify(hotelRepository)
                .findById(1L);

        verify(hotelRepository)
                .save(hotel);

        verify(hotelMapper)
                .toResponse(hotel);
    }

    @Test
    void updateHotel_shouldThrowExceptionWhenHotelNotFound() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> hotelService.updateHotel(
                        1L,
                        updateRequest)
        );

        verify(hotelRepository, never())
                .save(any());

        verify(hotelMapper, never())
                .toResponse(any());
    }

    @Test
    void getHotelById_shouldReturnHotel() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));

        when(hotelMapper.toResponse(hotel))
                .thenReturn(response);

        HotelResponse result =
                hotelService.getHotelById(1L);

        assertNotNull(result);
        assertEquals(response, result);

        verify(hotelRepository)
                .findById(1L);

        verify(hotelMapper)
                .toResponse(hotel);
    }

    @Test
    void getHotelById_shouldThrowExceptionWhenNotFound() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> hotelService.getHotelById(1L)
        );

        verify(hotelMapper, never())
                .toResponse(any());
    }

    @Test
    void getAllHotels_shouldReturnHotels() {

        Hotel secondHotel = new Hotel();
        secondHotel.setId(2L);
        secondHotel.setName("City Hotel");

        HotelResponse secondResponse =
                mock(HotelResponse.class);

        when(hotelRepository.findAll())
                .thenReturn(List.of(
                        hotel,
                        secondHotel
                ));

        when(hotelMapper.toResponse(hotel))
                .thenReturn(response);

        when(hotelMapper.toResponse(secondHotel))
                .thenReturn(secondResponse);

        List<HotelResponse> result =
                hotelService.getAllHotels();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response, result.get(0));
        assertEquals(secondResponse, result.get(1));

        verify(hotelRepository)
                .findAll();

        verify(hotelMapper)
                .toResponse(hotel);

        verify(hotelMapper)
                .toResponse(secondHotel);
    }

    @Test
    void getAllHotels_shouldReturnEmptyListWhenNoHotels() {

        when(hotelRepository.findAll())
                .thenReturn(List.of());

        List<HotelResponse> result =
                hotelService.getAllHotels();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hotelRepository)
                .findAll();

        verify(hotelMapper, never())
                .toResponse(any());
    }

    @Test
    void deleteHotel_shouldDeleteSuccessfully() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));

        hotelService.deleteHotel(1L);

        verify(hotelRepository)
                .findById(1L);

        verify(hotelRepository)
                .delete(hotel);
    }

    @Test
    void deleteHotel_shouldThrowExceptionWhenNotFound() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> hotelService.deleteHotel(1L)
        );

        verify(hotelRepository, never())
                .delete(any());
    }
}