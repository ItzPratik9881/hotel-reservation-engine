package com.pratik.hotelreservation.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.info("Incoming request: {} {}", method, uri);

        try {

            filterChain.doFilter(request, response);

        } finally {

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "Completed request: {} {} | Status: {} | Duration: {} ms",
                    method,
                    uri,
                    response.getStatus(),
                    duration
            );
        }
    }
}