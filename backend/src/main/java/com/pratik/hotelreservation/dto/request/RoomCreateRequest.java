package com.pratik.hotelreservation.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

import com.pratik.hotelreservation.enums.RoomType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreateRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull
    private RoomType roomType;  

    @NotNull(message = "Capacity is required")
    @Min(value = 1)
    private Integer capacity;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01")
    private BigDecimal pricePerNight;

    @NotNull(message = "Hotel Id is required")
    private Long hotelId;
}