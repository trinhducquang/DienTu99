package org.example.dientu99.dto.productDTO;

import java.math.BigDecimal;

public record ProductDisplayInfoDTO(
        int id,
        String name,
        String imageUrl,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        int stockQuantity
) {
}

