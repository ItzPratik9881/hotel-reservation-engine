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
import com.pratik.hotelreservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;

    @Override
    public RoomResponse createRoom(RoomCreateRequest request) {

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found"));

        if (roomRepository.existsByRoomNumberAndHotelId(
                request.getRoomNumber(),
                request.getHotelId())) {

            throw new DuplicateResourceException(
                    "Room number already exists in this hotel");
        }

        Room room = roomMapper.toEntity(request);

        room.setHotel(hotel);
        room.setAvailable(true);

        Room savedRoom = roomRepository.save(room);

        return roomMapper.toResponse(savedRoom);
    }

    @Override
    public RoomResponse getRoomById(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        return roomMapper.toResponse(room);
    }

    @Override
    public List<RoomResponse> getAllRooms() {

        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    public List<RoomResponse> getRoomsByHotel(Long hotelId) {

        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    public RoomResponse updateRoom(Long id,
                                   RoomUpdateRequest request) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        roomMapper.updateEntity(request, room);

        Room updatedRoom = roomRepository.save(room);

        return roomMapper.toResponse(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found"));

        roomRepository.delete(room);
    }
}