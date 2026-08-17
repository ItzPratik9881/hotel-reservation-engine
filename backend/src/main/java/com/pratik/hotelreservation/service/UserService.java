package com.pratik.hotelreservation.service;

import com.pratik.hotelreservation.dto.request.RegisterRequest;
import com.pratik.hotelreservation.dto.response.RegisterResponse;

public interface UserService {

    RegisterResponse register(RegisterRequest request);

    RegisterResponse getCurrentUser(String email);
}