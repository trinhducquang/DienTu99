package org.example.dientu99.dto.orderDTO;

import org.example.dientu99.enums.ExportStatus;
import org.example.dientu99.enums.OrderStatus;

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
        String note,
        ExportStatus exportStatus
) {

}
