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
public class RoomUpdateRequest {

    @NotBlank
    private String roomNumber;

    @NotNull
    private RoomType roomType;

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal pricePerNight;

    @NotNull
    private Boolean available;
}