 package org.example.quanlybanhang.utils;

 import org.example.quanlybanhang.dto.OrderSummaryDTO;
 import org.example.quanlybanhang.dto.ProductDisplayInfoDTO;

 import java.util.List;

 public class OrderConverter {
    public static List<ProductDisplayInfoDTO> toProductDisplayInfoList(OrderSummaryDTO summary) {
        if (summary == null) return List.of();

        String[] ids = summary.getProductIds().split(",");
        String[] names = summary.getProductNames().split(",\\s*");
        String[] images = summary.getProductImages().split(",\\s*");
        String[] quantities = summary.getProductQuantities().split(",");
        String[] prices = summary.getProductPrices().split(",");

        List<ProductDisplayInfoDTO> productList = new java.util.ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            int id = i < ids.length ? Integer.parseInt(ids[i]) : -1;
            String name = names[i];
            String image = i < images.length ? images[i] : null;
            int quantity = i < quantities.length ? Integer.parseInt(quantities[i]) : 0;
            double price = i < prices.length ? Double.parseDouble(prices[i]) : 0;
            double total = quantity * price;

            productList.add(new ProductDisplayInfoDTO(id, name, image, quantity, price, total));
        }

        return productList;
    }
}
