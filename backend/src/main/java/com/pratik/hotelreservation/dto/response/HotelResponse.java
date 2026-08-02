package com.pratik.hotelreservation.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponse {

    private Long id;

    private String name;

    private String address;

    private String city;

    private String state;

    private String country;

    private Integer starRating;

    private String description;

    private Boolean active;
}