package org.example.quanlybanhang.dto.productDTO;

import java.math.BigDecimal;

public record ProductDisplayInfoDTO(
        int id,
        String name,
        String imageUrl,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
