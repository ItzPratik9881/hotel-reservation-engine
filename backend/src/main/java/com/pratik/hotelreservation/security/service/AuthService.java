package com.pratik.hotelreservation.security.service;

import com.pratik.hotelreservation.dto.request.LoginRequest;
import com.pratik.hotelreservation.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}