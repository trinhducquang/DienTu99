package org.example.quanlybanhang.utils;

import org.example.quanlybanhang.dto.OrderSummaryDTO;
import org.example.quanlybanhang.dto.ProductDisplayInfoDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderConverter {

    public static List<ProductDisplayInfoDTO> toProductDisplayInfoList(OrderSummaryDTO summary) {
        if (summary == null) return Collections.emptyList();

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
            int quantity = parseSafeInt(getSafe(quantities, i), 0);
            double unitPrice = parseSafeDouble(getSafe(prices, i));
            double total = quantity * unitPrice;

            productList.add(new ProductDisplayInfoDTO(id, name, image, quantity, unitPrice, total));
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

    private static double parseSafeDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
