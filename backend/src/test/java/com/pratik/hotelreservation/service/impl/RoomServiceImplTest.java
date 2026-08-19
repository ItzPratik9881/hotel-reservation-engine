package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.request.RoomCreateRequest;
import com.pratik.hotelreservation.dto.request.RoomUpdateRequest;
import com.pratik.hotelreservation.dto.response.RoomResponse;
import com.pratik.hotelreservation.entity.Hotel;
import com.pratik.hotelreservation.entity.Room;
import com.pratik.hotelreservation.exception.DuplicateResourceException;
import com.pratik.hotelreservation.exception.ResourceNotFoundException;
import com.pratik.hotelreservation.mapper.RoomMapper;
import com.pratik.hotelreservation.repository.HotelRepository;
import com.pratik.hotelreservation.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    private RoomCreateRequest createRequest;
    private RoomUpdateRequest updateRequest;
    private Hotel hotel;
    private Room room;
    private RoomResponse response;

    @BeforeEach
    void setUp() {

        createRequest = new RoomCreateRequest();

        createRequest.setHotelId(1L);
        createRequest.setRoomNumber("101");
        createRequest.setPricePerNight(
                new BigDecimal("2500.00"));
        createRequest.setCapacity(2);

        updateRequest = new RoomUpdateRequest();

        updateRequest.setRoomNumber("102");
        updateRequest.setPricePerNight(
                new BigDecimal("3000.00"));
        updateRequest.setCapacity(3);
        updateRequest.setAvailable(true);

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Grand Palace");

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setHotel(hotel);
        room.setPricePerNight(
                new BigDecimal("2500.00"));
        room.setCapacity(2);
        room.setAvailable(true);

        response = mock(RoomResponse.class);
    }

    @Test
    void createRoom_shouldCreateSuccessfully() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));

        when(roomRepository.existsByRoomNumberAndHotelId(
                "101", 1L))
                .thenReturn(false);

        when(roomMapper.toEntity(createRequest))
                .thenReturn(room);

        when(roomRepository.save(room))
                .thenReturn(room);

        when(roomMapper.toResponse(room))
                .thenReturn(response);

        RoomResponse result =
                roomService.createRoom(createRequest);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(hotel, room.getHotel());
        assertTrue(room.getAvailable());

        verify(hotelRepository)
                .findById(1L);

        verify(roomRepository)
                .existsByRoomNumberAndHotelId("101", 1L);

        verify(roomMapper)
                .toEntity(createRequest);

        verify(roomRepository)
                .save(room);

        verify(roomMapper)
                .toResponse(room);
    }

    @Test
    void createRoom_shouldThrowExceptionWhenHotelNotFound() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.createRoom(createRequest)
        );

        verify(roomRepository, never())
                .existsByRoomNumberAndHotelId(anyString(), anyLong());

        verify(roomRepository, never())
                .save(any());
    }

    @Test
    void createRoom_shouldThrowExceptionWhenRoomNumberAlreadyExists() {

        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));

        when(roomRepository.existsByRoomNumberAndHotelId(
                "101", 1L))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> roomService.createRoom(createRequest)
                );

        assertEquals(
                "Room number already exists in this hotel",
                exception.getMessage());

        verify(roomMapper, never())
                .toEntity(any());

        verify(roomRepository, never())
                .save(any());
    }

    @Test
    void getRoomById_shouldReturnRoom() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(roomMapper.toResponse(room))
                .thenReturn(response);

        RoomResponse result =
                roomService.getRoomById(1L);

        assertNotNull(result);
        assertEquals(response, result);

        verify(roomRepository)
                .findById(1L);

        verify(roomMapper)
                .toResponse(room);
    }

    @Test
    void getRoomById_shouldThrowExceptionWhenNotFound() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.getRoomById(1L)
        );

        verify(roomMapper, never())
                .toResponse(any());
    }

    @Test
    void getAllRooms_shouldReturnRooms() {

        Room secondRoom = new Room();
        secondRoom.setId(2L);
        secondRoom.setRoomNumber("102");

        RoomResponse secondResponse =
                mock(RoomResponse.class);

        when(roomRepository.findAll())
                .thenReturn(List.of(
                        room,
                        secondRoom
                ));

        when(roomMapper.toResponse(room))
                .thenReturn(response);

        when(roomMapper.toResponse(secondRoom))
                .thenReturn(secondResponse);

        List<RoomResponse> result =
                roomService.getAllRooms();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response, result.get(0));
        assertEquals(secondResponse, result.get(1));

        verify(roomRepository)
                .findAll();
    }

    @Test
    void getRoomsByHotel_shouldReturnRooms() {

        when(roomRepository.findByHotelId(1L))
                .thenReturn(List.of(room));

        when(roomMapper.toResponse(room))
                .thenReturn(response);

        List<RoomResponse> result =
                roomService.getRoomsByHotel(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(roomRepository)
                .findByHotelId(1L);

        verify(roomMapper)
                .toResponse(room);
    }

    @Test
    void updateRoom_shouldUpdateSuccessfully() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(roomRepository.save(room))
                .thenReturn(room);

        when(roomMapper.toResponse(room))
                .thenReturn(response);

        RoomResponse result =
                roomService.updateRoom(
                        1L,
                        updateRequest);

        assertNotNull(result);
        assertEquals(response, result);

        verify(roomRepository)
                .findById(1L);

        verify(roomMapper)
                .updateEntity(updateRequest, room);

        verify(roomRepository)
                .save(room);

        verify(roomMapper)
                .toResponse(room);
    }

    @Test
    void updateRoom_shouldThrowExceptionWhenNotFound() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.updateRoom(
                        1L,
                        updateRequest)
        );

        verify(roomMapper, never())
                .updateEntity(any(), any());

        verify(roomRepository, never())
                .save(any());
    }

    @Test
    void deleteRoom_shouldDeleteSuccessfully() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        roomService.deleteRoom(1L);

        verify(roomRepository)
                .findById(1L);

        verify(roomRepository)
                .delete(room);
    }

    @Test
    void deleteRoom_shouldThrowExceptionWhenNotFound() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.deleteRoom(1L)
        );

        verify(roomRepository, never())
                .delete(any());
    }
}