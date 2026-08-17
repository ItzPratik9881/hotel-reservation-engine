package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import com.pratik.hotelreservation.dto.request.LoginRequest;
import com.pratik.hotelreservation.dto.request.RegisterRequest;
import com.pratik.hotelreservation.dto.response.LoginResponse;
import com.pratik.hotelreservation.dto.response.RegisterResponse;
import com.pratik.hotelreservation.security.service.AuthService;
import com.pratik.hotelreservation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "User registration and JWT authentication APIs"
)
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account in the Hotel Reservation Engine."
    )
    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ApiResponse.success(
                "User registered successfully",
                userService.register(request)
        );
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates the user and returns a JWT token."
    )
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ApiResponse.success(
                "Login successful",
                authService.login(request)
        );
    }

    @Operation(
            summary = "Get current user",
            description = "Returns the currently authenticated user's details."
    )
    @GetMapping("/me")
    public ApiResponse<RegisterResponse> getCurrentUser(
            Authentication authentication) {

        return ApiResponse.success(
                "Current user fetched successfully",
                userService.getCurrentUser(authentication.getName())
        );
    }

    @Operation(
            summary = "Test authentication",
            description = "Verifies that JWT authentication is working correctly."
    )
    @GetMapping("/hello")
    public ApiResponse<String> hello() {

        return ApiResponse.success(
                "Authentication test successful",
                "JWT Authentication Working Successfully!"
        );
    }
}