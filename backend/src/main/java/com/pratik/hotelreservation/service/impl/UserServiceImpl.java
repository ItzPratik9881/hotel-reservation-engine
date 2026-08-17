package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.request.RegisterRequest;
import com.pratik.hotelreservation.dto.response.RegisterResponse;
import com.pratik.hotelreservation.entity.User;
import com.pratik.hotelreservation.enums.Role;
import com.pratik.hotelreservation.exception.DuplicateResourceException;
import com.pratik.hotelreservation.mapper.UserMapper;
import com.pratik.hotelreservation.repository.UserRepository;
import com.pratik.hotelreservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public RegisterResponse getCurrentUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return userMapper.toResponse(user);
    }
}