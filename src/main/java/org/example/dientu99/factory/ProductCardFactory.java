package org.example.dientu99.factory;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.dientu99.controller.sale.manager.CartManager;
import org.example.dientu99.dto.productDTO.ProductDisplayInfoDTO;
import org.example.dientu99.model.Product;
import org.example.dientu99.utils.ImagesUtils;
import org.example.dientu99.utils.MoneyUtils;


import java.math.BigDecimal;

public class ProductCardFactory {

    private final CartManager cartManager;

    public ProductCardFactory(CartManager cartManager) {
        this.cartManager = cartManager;
    }

    public VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setSpacing(10);
        VBox contentBox = new VBox(10);
        contentBox.getStyleClass().add("product-content");
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        // Image view setup
        ImageView imageView = ImagesUtils.createCroppedImageView(
                product.getImageUrl(), 260, 220, 220, 160);
        StackPane imagePane = new StackPane(imageView);
        imagePane.setPrefHeight(130);
        imagePane.getStyleClass().add("product-image-pane");
        Label name = new Label(product.getName());
        name.setWrapText(true);
        name.getStyleClass().add("product-name");
        Label price = new Label(MoneyUtils.formatVN(product.getPrice()));
        price.getStyleClass().add("related-product-price");
        contentBox.getChildren().addAll(imagePane, name, price);
        Button addToCart = new Button("Thêm vào giỏ");
        addToCart.setMaxWidth(Double.MAX_VALUE);
        addToCart.getStyleClass().add("add-to-cart-button");
        if (product.getStockQuantity() <= 0) {
            addToCart.setDisable(true);
            addToCart.setOpacity(0.5);
            addToCart.setText("Hết hàng");
        }

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
        card.getChildren().addAll(contentBox, addToCart);
        return card;
    }
}