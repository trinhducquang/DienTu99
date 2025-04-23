package org.example.quanlybanhang.factory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.quanlybanhang.controller.sale.manager.CartManager;
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.utils.AlertUtils;

import java.math.BigDecimal;

import static org.example.quanlybanhang.utils.TextFieldFormatterUtils.formatCurrency;

public class CartItemFactory {

    private final CartManager cartManager;

    public CartItemFactory(CartManager cartManager) {
        this.cartManager = cartManager;
    }

    public VBox createCartItemBox(ProductDisplayInfoDTO dto) {
        ImageView imageView = new ImageView(new Image(dto.imageUrl()));
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(dto.name());
        nameLabel.getStyleClass().add("cart-item-label");

        Label priceLabel = new Label(formatCurrency(dto.unitPrice()));
        priceLabel.getStyleClass().add("cart-item-price");

        VBox infoBox = new VBox(nameLabel, priceLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Button deleteBtn = new Button("X");
        deleteBtn.getStyleClass().addAll("cart-item-button", "delete");

        HBox topBox = new HBox(10, imageView, infoBox, deleteBtn);
        topBox.setAlignment(Pos.CENTER_LEFT);

        Button minusBtn = new Button("-");
        Button plusBtn = new Button("+");
        TextField quantityField = new TextField(dto.quantity().toPlainString());
        quantityField.setPrefWidth(40);
        quantityField.setAlignment(Pos.CENTER);

        Label totalItemLabel = new Label(formatCurrency(dto.unitPrice().multiply(dto.quantity())));
        totalItemLabel.getStyleClass().add("cart-item-price");

        int maxStockQuantity = dto.stockQuantity();

        if (dto.quantity().compareTo(new BigDecimal(maxStockQuantity)) >= 0) {
            plusBtn.setDisable(true);
        }

        minusBtn.setOnAction(e -> {
            BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
            if (currentQuantity.compareTo(BigDecimal.ONE) > 0) {
                currentQuantity = currentQuantity.subtract(BigDecimal.ONE);
                quantityField.setText(currentQuantity.toPlainString());
                cartManager.updateItemTotal((VBox) minusBtn.getParent().getParent(), dto.unitPrice(), currentQuantity);
                cartManager.decreaseItemQuantity(dto.id());
                if (currentQuantity.compareTo(new BigDecimal(maxStockQuantity)) < 0) {
                    plusBtn.setDisable(false);
                }
            }
        });

        plusBtn.setOnAction(e -> {
            BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
            if (currentQuantity.compareTo(new BigDecimal(maxStockQuantity)) < 0) {
                currentQuantity = currentQuantity.add(BigDecimal.ONE);
                quantityField.setText(currentQuantity.toPlainString());
                cartManager.updateItemTotal((VBox) plusBtn.getParent().getParent(), dto.unitPrice(), currentQuantity);
                cartManager.increaseItemQuantity(dto.id());
                if (currentQuantity.compareTo(new BigDecimal(maxStockQuantity)) >= 0) {
                    plusBtn.setDisable(true);
                }
            } else {
                AlertUtils.showWarning(
                        "Giới hạn tồn kho",
                        "Tồn kho chỉ còn " + maxStockQuantity + " sản phẩm. Không thể thêm số lượng nhiều hơn số lượng tồn kho."
                );
            }
        });

        quantityField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                quantityField.setText(oldText);
                return;
            }

            try {
                if (!newText.isEmpty()) {
                    BigDecimal newQuantity = new BigDecimal(newText);
                    if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                        quantityField.setText("1");
                        newQuantity = BigDecimal.ONE;
                    } else if (newQuantity.compareTo(new BigDecimal(maxStockQuantity)) > 0) {
                        quantityField.setText(String.valueOf(maxStockQuantity));
                        newQuantity = new BigDecimal(maxStockQuantity);
                        AlertUtils.showWarning(
                                "Giới hạn tồn kho",
                                "Tồn kho chỉ còn " + maxStockQuantity + " sản phẩm. Số lượng đã được điều chỉnh về mức tối đa."
                        );
                    }
                    plusBtn.setDisable(newQuantity.compareTo(new BigDecimal(maxStockQuantity)) >= 0);
                    cartManager.updateItemTotal((VBox) quantityField.getParent().getParent(), dto.unitPrice(), newQuantity);
                    cartManager.updateCartTotal();
                }
            } catch (NumberFormatException e) {
                quantityField.setText("1");
            }
        });

        HBox quantityBox = new HBox(5, minusBtn, quantityField, plusBtn,
                new Label("Thành tiền:"), totalItemLabel);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        VBox itemBox = new VBox(5, topBox, quantityBox);
        itemBox.getStyleClass().add("cart-item");
        itemBox.setPadding(new Insets(10));

        quantityField.getStyleClass().add("cart-item-textfield");
        minusBtn.getStyleClass().add("cart-item-button");
        plusBtn.getStyleClass().add("cart-item-button");

        deleteBtn.setOnAction(e -> {
            cartManager.removeCartItem(dto.id());
            VBox parentBox = (VBox) deleteBtn.getParent().getParent();
            cartManager.getCartItems().removeIf(item -> item.getProduct().getId() == dto.id());
            ((VBox) parentBox.getParent()).getChildren().remove(parentBox);
        });

        return itemBox;
    }
}
