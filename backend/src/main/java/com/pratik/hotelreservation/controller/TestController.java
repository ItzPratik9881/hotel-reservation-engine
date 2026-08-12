package com.pratik.hotelreservation.controller;

import com.pratik.hotelreservation.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/v1/test")
    public ApiResponse<String> test() {

        return ApiResponse.success(
                "Protected API access successful",
                "Protected API Access Granted!"
        );
    }
}