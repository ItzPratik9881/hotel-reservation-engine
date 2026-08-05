package com.pratik.hotelreservation.mapper;

import com.pratik.hotelreservation.dto.response.PaymentResponse;
import com.pratik.hotelreservation.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "reservation.id", target = "reservationId")
    PaymentResponse toResponse(Payment payment);

}