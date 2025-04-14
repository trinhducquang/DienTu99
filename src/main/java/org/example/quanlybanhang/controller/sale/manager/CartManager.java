package org.example.quanlybanhang.controller.sale.manager;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.dto.productDTO.CartItem;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.utils.MoneyUtils;

import java.math.BigDecimal;

import static org.example.quanlybanhang.utils.TextFieldFormatterUtils.formatCurrency;

public class CartManager {
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final IntegerProperty cartItemCount = new SimpleIntegerProperty(0);
    private final VBox cartItemsContainer;
    private final Label totalLabel;

    public CartManager(VBox cartItemsContainer, Label totalLabel) {
        this.cartItemsContainer = cartItemsContainer;
        this.totalLabel = totalLabel;
    }

    public void addToCart(ProductDisplayInfoDTO dto) {
        boolean exists = false;
        for (Node node : cartItemsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox itemBox = (VBox) node;
                HBox topBox = (HBox) itemBox.getChildren().get(0);
                Label nameLabel = (Label) ((VBox) topBox.getChildren().get(1)).getChildren().get(0);
                if (nameLabel.getText().equals(dto.name())) {
                    // Sản phẩm đã tồn tại, tăng số lượng
                    TextField quantityField = (TextField) ((HBox) itemBox.getChildren().get(1)).getChildren().get(1);
                    BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
                    currentQuantity = currentQuantity.add(BigDecimal.ONE);
                    quantityField.setText(currentQuantity.toPlainString());
                    updateItemTotal(itemBox, dto.unitPrice(), currentQuantity);
                    exists = true;
                    break;
                }
            }
        }

        if (!exists) {
            // Nếu sản phẩm chưa có trong giỏ, tạo mới và thêm vào
            VBox itemBox = createCartItemBox(dto);
            cartItemsContainer.getChildren().add(itemBox);

            // Cập nhật danh sách CartItem
            CartItem cartItem = new CartItem(new Product(dto.id(), dto.name(), dto.imageUrl(), dto.unitPrice()), dto.quantity());
            cartItems.add(cartItem);
            cartItemCount.set(cartItems.size());
        }

        updateCartTotal();
    }

    public void removeFromCart(int productId) {
        // Xóa khỏi UI và danh sách cartItems
        cartItemsContainer.getChildren().removeIf(node -> {
            if (node instanceof VBox) {
                VBox itemBox = (VBox) node;
                HBox topBox = (HBox) itemBox.getChildren().get(0);
                Label nameLabel = (Label) ((VBox) topBox.getChildren().get(1)).getChildren().get(0);

                // Tìm CartItem tương ứng để lấy ID
                for (CartItem item : cartItems) {
                    if (item.getProduct().getId() == productId && item.getProduct().getName().equals(nameLabel.getText())) {
                        return true;
                    }
                }
            }
            return false;
        });

        // Xóa khỏi danh sách CartItem
        cartItems.removeIf(item -> item.getProduct().getId() == productId);
        cartItemCount.set(cartItems.size());

        updateCartTotal();
    }

    public void updateQuantity(int productId, BigDecimal quantity) {
        // Logic cập nhật số lượng
        for (Node node : cartItemsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox itemBox = (VBox) node;
                HBox topBox = (HBox) itemBox.getChildren().get(0);
                VBox infoBox = (VBox) topBox.getChildren().get(1);
                Label nameLabel = (Label) infoBox.getChildren().get(0);
                Label priceLabel = (Label) infoBox.getChildren().get(1);

                // Kiểm tra sản phẩm cần cập nhật
                for (CartItem item : cartItems) {
                    if (item.getProduct().getId() == productId && item.getProduct().getName().equals(nameLabel.getText())) {
                        // Cập nhật số lượng
                        HBox quantityBox = (HBox) itemBox.getChildren().get(1);
                        TextField quantityField = (TextField) quantityBox.getChildren().get(1);
                        quantityField.setText(quantity.toPlainString());

                        // Cập nhật thành tiền
                        BigDecimal unitPrice = MoneyUtils.parseCurrencyText(priceLabel.getText());
                        updateItemTotal(itemBox, unitPrice, quantity);

                        // Cập nhật CartItem
                        item.setQuantity(quantity);
                        break;
                    }
                }
            }
        }

        updateCartTotal();
    }

    private VBox createCartItemBox(ProductDisplayInfoDTO dto) {
        ImageView imageView = new ImageView(new Image(dto.imageUrl()));
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(dto.name());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label priceLabel = new Label(formatCurrency(dto.unitPrice()));
        priceLabel.setStyle("-fx-text-fill: #e74c3c;");

        VBox infoBox = new VBox(nameLabel, priceLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Button deleteBtn = new Button("X");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-min-width: 30;");

        HBox topBox = new HBox(10, imageView, infoBox, deleteBtn);

        // Khởi tạo nút - và +, trường số lượng
        Button minusBtn = new Button("-");
        Button plusBtn = new Button("+");
        TextField quantityField = new TextField(dto.quantity().toPlainString());
        quantityField.setPrefWidth(40);
        quantityField.setAlignment(Pos.CENTER);

        // Tạo label để hiển thị thành tiền
        Label totalItemLabel = new Label(formatCurrency(dto.unitPrice().multiply(dto.quantity())));
        totalItemLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // Xử lý sự kiện khi nhấn nút -
        minusBtn.setOnAction(e -> {
            BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
            if (currentQuantity.compareTo(BigDecimal.ONE) > 0) {
                currentQuantity = currentQuantity.subtract(BigDecimal.ONE);
                quantityField.setText(currentQuantity.toPlainString());
                updateItemTotal((VBox) minusBtn.getParent().getParent(), dto.unitPrice(), currentQuantity);

                // Cập nhật đối tượng CartItem
                for (CartItem item : cartItems) {
                    if (item.getProduct().getId() == dto.id()) {
                        item.decreaseQuantity();
                        break;
                    }
                }

                updateCartTotal();
            }
        });

        // Xử lý sự kiện khi nhấn nút +
        plusBtn.setOnAction(e -> {
            BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
            currentQuantity = currentQuantity.add(BigDecimal.ONE);
            quantityField.setText(currentQuantity.toPlainString());
            updateItemTotal((VBox) plusBtn.getParent().getParent(), dto.unitPrice(), currentQuantity);

            // Cập nhật đối tượng CartItem
            for (CartItem item : cartItems) {
                if (item.getProduct().getId() == dto.id()) {
                    item.increaseQuantity();
                    break;
                }
            }

            updateCartTotal();
        });

        // Xử lý sự kiện khi thay đổi số lượng trực tiếp trong TextField
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
                    updateItemTotal((VBox) quantityField.getParent().getParent(), dto.unitPrice(), newQuantity);
                    updateCartTotal();
                }
            } catch (NumberFormatException e) {
                quantityField.setText("1");
            }
        });

        HBox quantityBox = new HBox(5, minusBtn, quantityField, plusBtn, new Label("Thành tiền:"), totalItemLabel);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        VBox itemBox = new VBox(5, topBox, quantityBox);
        itemBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: white;");
        itemBox.setPadding(new Insets(10));

        // Xử lý khi nhấn nút X để xóa sản phẩm
        deleteBtn.setOnAction(e -> {
            cartItemsContainer.getChildren().remove(itemBox);

            // Xóa item khỏi danh sách CartItem
            cartItems.removeIf(item -> item.getProduct().getId() == dto.id());
            cartItemCount.set(cartItems.size());

            updateCartTotal();
        });

        return itemBox;
    }

    // Phương thức tính lại thành tiền của một sản phẩm trong giỏ
    private void updateItemTotal(VBox itemBox, BigDecimal unitPrice, BigDecimal quantity) {
        HBox quantityBox = (HBox) itemBox.getChildren().get(1);
        Label totalItemLabel = (Label) quantityBox.getChildren().get(5);
        BigDecimal totalPrice = unitPrice.multiply(quantity);
        totalItemLabel.setText(formatCurrency(totalPrice));
    }

    // Phương thức cập nhật tổng tiền của giỏ hàng
    public void updateCartTotal() {
        BigDecimal total = BigDecimal.ZERO;

        // Tính tổng tiền từ các sản phẩm trong giỏ hàng
        for (Node node : cartItemsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox itemBox = (VBox) node;
                HBox quantityBox = (HBox) itemBox.getChildren().get(1);
                TextField quantityField = (TextField) quantityBox.getChildren().get(1);

                HBox topBox = (HBox) itemBox.getChildren().get(0);
                VBox infoBox = (VBox) topBox.getChildren().get(1);
                Label priceLabel = (Label) infoBox.getChildren().get(1);

                BigDecimal quantity = new BigDecimal(quantityField.getText());
                BigDecimal unitPrice = MoneyUtils.parseCurrencyText(priceLabel.getText());

                total = total.add(unitPrice.multiply(quantity));
            }
        }

        totalLabel.setText(formatCurrency(total));
    }

    public IntegerProperty cartItemCountProperty() {
        return cartItemCount;
    }

    public ObservableList<CartItem> getCartItems() {
        return cartItems;
    }
}