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
        card.setStyle("-fx-border-color: #e0e0e0; -fx-background-color: white; -fx-padding: 10;");


        ImageView imageView = ImagesUtils.createCroppedImageView(
                product.getImageUrl(), 260, 220, 220, 160);
        StackPane imagePane = new StackPane(imageView);
        imagePane.setPrefHeight(130);
        imagePane.setStyle("-fx-alignment: center;");

        Label name = new Label(product.getName());
        name.setWrapText(true);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label price = new Label(MoneyUtils.formatVN(product.getPrice()));
        price.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 13;");

        Button addToCart = new Button("Thêm vào giỏ");
        addToCart.setMaxWidth(Double.MAX_VALUE);
        addToCart.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-weight: bold;");

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