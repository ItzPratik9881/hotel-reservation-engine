package com.pratik.hotelreservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponse {

    private BigDecimal todayRevenue;

    private BigDecimal monthlyRevenue;

    private BigDecimal yearlyRevenue;
}