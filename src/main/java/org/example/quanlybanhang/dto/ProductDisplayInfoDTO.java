package org.example.quanlybanhang.dto;

public record ProductDisplayInfoDTO(
        int id,
        String name,
        String imageUrl,
        int quantity,
        double unitPrice,
        double totalPrice
) {
}
