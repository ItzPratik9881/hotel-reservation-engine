package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.dto.request.RegisterRequest;
import com.pratik.hotelreservation.dto.response.RegisterResponse;
import com.pratik.hotelreservation.entity.User;
import com.pratik.hotelreservation.enums.Role;
import com.pratik.hotelreservation.exception.DuplicateResourceException;
import com.pratik.hotelreservation.mapper.UserMapper;
import com.pratik.hotelreservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest request;
    private User user;
    private RegisterResponse response;

    @BeforeEach
    void setUp() {

        request = new RegisterRequest();

        request.setFirstName("Pratik");
        request.setLastName("Kedari");
        request.setEmail("pratik@test.com");
        request.setPassword("password123");
        request.setPhoneNumber("9876543210");

        user = new User();

        user.setId(1L);
        user.setFirstName("Pratik");
        user.setLastName("Kedari");
        user.setEmail("pratik@test.com");
        user.setPassword("encoded-password");
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        response = mock(RegisterResponse.class);
    }

    @Test
    void register_shouldCreateUserSuccessfully() {

        when(userRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode(
                request.getPassword()))
                .thenReturn("encoded-password");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        RegisterResponse result =
                userService.register(request);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(
                "encoded-password",
                user.getPassword());

        assertEquals(
                Role.CUSTOMER,
                user.getRole());

        verify(userRepository).save(argThat(savedUser ->
        savedUser.isEnabled()
));

        verify(userRepository)
                .existsByEmail(request.getEmail());

        verify(passwordEncoder)
                .encode(request.getPassword());

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void register_shouldThrowExceptionWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> userService.register(request)
                );

        assertEquals(
                "Email already exists",
                exception.getMessage());

        verify(userMapper, never())
                .toEntity(any());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void register_shouldEncodePassword() {

        when(userRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode(
                request.getPassword()))
                .thenReturn("secure-hash");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        userService.register(request);

        assertEquals(
                "secure-hash",
                user.getPassword());

        verify(passwordEncoder)
                .encode("password123");
    }

    @Test
    void register_shouldAssignCustomerRole() {

        when(userRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded-password");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        userService.register(request);

        assertEquals(
                Role.CUSTOMER,
                user.getRole());
    }

    @Test
    void register_shouldEnableUser() {

        when(userRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded-password");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        userService.register(request);

        verify(userRepository).save(argThat(savedUser ->
        savedUser.isEnabled()
));
    }

    @Test
    void getCurrentUser_shouldReturnUser() {

        when(userRepository.findByEmail(
                "pratik@test.com"))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(response);

        RegisterResponse result =
                userService.getCurrentUser(
                        "pratik@test.com");

        assertNotNull(result);
        assertEquals(response, result);

        verify(userRepository)
                .findByEmail("pratik@test.com");

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void getCurrentUser_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail(
                "unknown@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.getCurrentUser(
                                "unknown@test.com")
                );

        assertEquals(
                "User not found",
                exception.getMessage());

        verify(userMapper, never())
                .toResponse(any());
    }
}