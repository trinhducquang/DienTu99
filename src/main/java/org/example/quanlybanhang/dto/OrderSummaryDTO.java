package org.example.quanlybanhang.dto;

import org.example.quanlybanhang.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryDTO(
        int id,
        String productIds,
        int employeeId,
        int customerId,
        String customerName,
        BigDecimal totalPrice,
        BigDecimal shippingFee,
        LocalDateTime orderDate,
        OrderStatus status,
        String productNames,
        String productImages,
        String productQuantities,
        String productPrices,
        String note
) {
}
