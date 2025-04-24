package org.example.dientu99.dto.EmployeeDTO;

import java.math.BigDecimal;

public record TopEmployee(
        Integer id,
        String fullName,
        Integer totalOrders,
        BigDecimal totalRevenue,
        BigDecimal totalProfit
) {}