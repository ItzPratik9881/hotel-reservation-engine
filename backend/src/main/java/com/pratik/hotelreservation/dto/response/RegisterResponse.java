package com.pratik.hotelreservation.dto.response;

import com.pratik.hotelreservation.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Role role;

    private Boolean enabled;
}