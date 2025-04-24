package org.example.dientu99.utils;

import org.example.dientu99.dto.orderDTO.OrderSummaryDTO;
import org.example.dientu99.dto.productDTO.ProductDisplayInfoDTO;
import org.example.dientu99.model.Product;
import org.example.dientu99.service.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderConverter {

    public static List<ProductDisplayInfoDTO> toProductDisplayInfoList(OrderSummaryDTO summary) {
        if (summary == null) return Collections.emptyList();

        // Khởi tạo ProductService ngay trong phương thức
        ProductService productService = new ProductService();

        String[] ids = safeSplit(summary.productIds());
        String[] names = safeSplit(summary.productNames());
        String[] images = safeSplit(summary.productImages());
        String[] quantities = safeSplit(summary.productQuantities());
        String[] prices = safeSplit(summary.productPrices());

        int maxLength = names.length;
        List<ProductDisplayInfoDTO> productList = new ArrayList<>();

        for (int i = 0; i < maxLength; i++) {
            int id = parseSafeInt(getSafe(ids, i), -1);
            String name = getSafe(names, i);
            String image = getSafe(images, i);

            BigDecimal quantity = parseSafeBigDecimal(getSafe(quantities, i), BigDecimal.ZERO);
            BigDecimal unitPrice = parseSafeBigDecimal(getSafe(prices, i), BigDecimal.ZERO);
            BigDecimal total = quantity.multiply(unitPrice);

            // Lấy thông tin stockQuantity từ database
            int stockQuantity = 0;
            if (id != -1) {
                Product product = productService.getProductById(id);
                if (product != null) {
                    stockQuantity = product.getStockQuantity();
                }
            }

            productList.add(new ProductDisplayInfoDTO(id, name, image, quantity, unitPrice, total, stockQuantity));
        }

        return productList;
    }

    private static String[] safeSplit(String input) {
        return (input == null || input.isBlank()) ? new String[0] : input.split(",\\s*");
    }

    private static String getSafe(String[] array, int index) {
        return index < array.length ? array[index] : "";
    }

    private static int parseSafeInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static BigDecimal parseSafeBigDecimal(String value, BigDecimal defaultValue) {
        try {
            return new BigDecimal(value.replaceAll("[^\\d.,-]", "").replace(",", ""));
        } catch (Exception e) {
            return defaultValue;
        }
    }


}
