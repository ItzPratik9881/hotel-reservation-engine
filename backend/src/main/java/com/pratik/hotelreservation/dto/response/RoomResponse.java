package com.pratik.hotelreservation.dto.response;

import lombok.*;

import java.math.BigDecimal;

import com.pratik.hotelreservation.enums.RoomType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;

    private String roomNumber;

    private RoomType roomType;

    private Integer capacity;

    private BigDecimal pricePerNight;

    private Boolean available;

    private Long hotelId;
}