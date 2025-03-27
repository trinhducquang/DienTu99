package org.example.quanlybanhang.service;

import org.example.quanlybanhang.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class SearchService {

    public static List<Product> searchProducts(List<Product> products, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return products;
        }

        String lowerKeyword = keyword.toLowerCase();
        return products.stream()
                .filter(product -> product.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
}
