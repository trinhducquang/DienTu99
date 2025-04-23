package org.example.quanlybanhang.factory;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.quanlybanhang.controller.sale.manager.CartManager;
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.utils.ImagesUtils;
import org.example.quanlybanhang.utils.MoneyUtils;


import java.math.BigDecimal;

public class ProductCardFactory {

    private final CartManager cartManager;

    public ProductCardFactory(CartManager cartManager) {
        this.cartManager = cartManager;
    }

    public VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");

        ImageView imageView = ImagesUtils.createCroppedImageView(
                product.getImageUrl(), 260, 220, 220, 160);
        StackPane imagePane = new StackPane(imageView);
        imagePane.setPrefHeight(130);
        imagePane.getStyleClass().add("product-image-pane");

        Label name = new Label(product.getName());
        name.setWrapText(true);
        name.getStyleClass().add("product-name");

        Label price = new Label(MoneyUtils.formatVN(product.getPrice()));
        price.getStyleClass().add("product-price");

        Button addToCart = new Button("Thêm vào giỏ");
        addToCart.setMaxWidth(Double.MAX_VALUE);
        addToCart.getStyleClass().add("add-to-cart-button");

        addToCart.setOnAction(e -> {
            ProductDisplayInfoDTO dto = new ProductDisplayInfoDTO(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    BigDecimal.ONE,
                    product.getPrice(),
                    product.getPrice().multiply(BigDecimal.ONE),
                    product.getStockQuantity()
            );
            cartManager.addToCart(dto);
        });

        VBox.setVgrow(addToCart, Priority.ALWAYS);
        card.getChildren().addAll(imagePane, name, price, addToCart);
        return card;
    }
}