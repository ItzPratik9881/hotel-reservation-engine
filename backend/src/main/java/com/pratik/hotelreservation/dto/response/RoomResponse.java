package com.pratik.hotelreservation.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;

    private String roomNumber;

    private String roomType;

    private Integer capacity;

    private BigDecimal pricePerNight;

    private Boolean available;

    private Long hotelId;
}