package org.example.quanlybanhang.controller.factory;

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
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.example.quanlybanhang.utils.TextFieldFormatterUtils.formatCurrency;

public class CartItemFactory {

    public static VBox createCartItemBox(
            ProductDisplayInfoDTO dto,
            Consumer<Integer> onRemoveItem,
            BiConsumer<Integer, BigDecimal> onQuantityChanged) {

        ImageView imageView = new ImageView(new Image(dto.imageUrl()));
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        // Product name and price info
        Label nameLabel = new Label(dto.name());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label priceLabel = new Label(formatCurrency(dto.unitPrice()));
        priceLabel.setStyle("-fx-text-fill: #e74c3c;");

        VBox infoBox = new VBox(nameLabel, priceLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Delete button
        Button deleteBtn = new Button("X");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-min-width: 30;");
        deleteBtn.setOnAction(e -> onRemoveItem.accept(dto.id()));

        // Top portion of the cart item
        HBox topBox = new HBox(10, imageView, infoBox, deleteBtn);

        // Quantity control
        Button minusBtn = new Button("-");
        Button plusBtn = new Button("+");
        TextField quantityField = new TextField(dto.quantity().toPlainString());
        quantityField.setPrefWidth(40);
        quantityField.setAlignment(Pos.CENTER);

        // Item total label
        Label totalItemLabel = new Label(formatCurrency(dto.unitPrice().multiply(dto.quantity())));
        totalItemLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // Quantity controls event handling
        minusBtn.setOnAction(e -> {
            BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
            if (currentQuantity.compareTo(BigDecimal.ONE) > 0) {
                currentQuantity = currentQuantity.subtract(BigDecimal.ONE);
                quantityField.setText(currentQuantity.toPlainString());
                onQuantityChanged.accept(dto.id(), currentQuantity);
            }
        });

        plusBtn.setOnAction(e -> {
            BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
            currentQuantity = currentQuantity.add(BigDecimal.ONE);
            quantityField.setText(currentQuantity.toPlainString());
            onQuantityChanged.accept(dto.id(), currentQuantity);
        });

        // Quantity text field change listener
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
                    }
                    onQuantityChanged.accept(dto.id(), newQuantity);
                }
            } catch (NumberFormatException e) {
                quantityField.setText("1");
            }
        });

        // Bottom row with quantity controls and total
        HBox quantityBox = new HBox(5, minusBtn, quantityField, plusBtn, new Label("Thành tiền:"), totalItemLabel);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        // Main container for the cart item
        VBox itemBox = new VBox(5, topBox, quantityBox);
        itemBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: white;");
        itemBox.setPadding(new Insets(10));

        return itemBox;
    }

    public static void updateItemTotal(VBox itemBox, BigDecimal unitPrice, BigDecimal quantity) {
        HBox quantityBox = (HBox) itemBox.getChildren().get(1);
        Label totalItemLabel = (Label) quantityBox.getChildren().get(5);
        BigDecimal totalPrice = unitPrice.multiply(quantity);
        totalItemLabel.setText(formatCurrency(totalPrice));
    }
}