package org.example.quanlybanhang.controller.sale.manager;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.controller.factory.CartItemFactory;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.dto.productDTO.CartItem;
import org.example.quanlybanhang.service.CartService;
import org.example.quanlybanhang.utils.MoneyUtils;

import java.math.BigDecimal;

import static org.example.quanlybanhang.utils.TextFieldFormatterUtils.formatCurrency;

public class CartManager {

    private final ObservableList<CartItem> cartItems;
    private final IntegerProperty cartItemCount;
    private final VBox cartItemsContainer;
    private final Label totalLabel;
    private final CartItemFactory cartItemFactory;
    private final CartService cartService;

    public CartManager(ObservableList<CartItem> cartItems,
                       IntegerProperty cartItemCount,
                       VBox cartItemsContainer,
                       Label totalLabel,
                       CartService cartService) {
        this.cartItems = cartItems;
        this.cartItemCount = cartItemCount;
        this.cartItemsContainer = cartItemsContainer;
        this.totalLabel = totalLabel;
        this.cartItemFactory = new CartItemFactory(this);
        this.cartService = cartService;
    }

    public void addToCart(ProductDisplayInfoDTO dto) {
        boolean exists = false;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == dto.id()) {
                // Sản phẩm đã tồn tại, tăng số lượng
                increaseItemQuantity(dto.id());

                // Cập nhật UI
                for (Node node : cartItemsContainer.getChildren()) {
                    if (node instanceof VBox) {
                        VBox itemBox = (VBox) node;
                        HBox topBox = (HBox) itemBox.getChildren().get(0);
                        Label nameLabel = (Label) ((VBox) topBox.getChildren().get(1)).getChildren().get(0);
                        if (nameLabel.getText().equals(dto.name())) {
                            TextField quantityField = (TextField) ((HBox) itemBox.getChildren().get(1)).getChildren().get(1);
                            quantityField.setText(item.getQuantity().toPlainString());
                            updateItemTotal(itemBox, dto.unitPrice(), item.getQuantity());
                            break;
                        }
                    }
                }

                exists = true;
                break;
            }
        }

        if (!exists) {
            // Nếu sản phẩm chưa có trong giỏ, tạo mới và thêm vào
            VBox itemBox = cartItemFactory.createCartItemBox(dto);
            cartItemsContainer.getChildren().add(itemBox);

            // Cập nhật danh sách CartItem
            CartItem cartItem = new CartItem(
                    new Product(dto.id(), dto.name(), dto.imageUrl(), dto.unitPrice()),
                    dto.quantity()
            );
            cartItems.add(cartItem);
            cartItemCount.set(cartItems.size());
        }

        updateCartTotal();
    }

    public void updateItemTotal(VBox itemBox, BigDecimal unitPrice, BigDecimal quantity) {
        HBox quantityBox = (HBox) itemBox.getChildren().get(1);
        Label totalItemLabel = (Label) quantityBox.getChildren().get(4); // Sửa từ 5 thành 4
        BigDecimal totalPrice = unitPrice.multiply(quantity);
        totalItemLabel.setText(formatCurrency(totalPrice));
    }

    public void updateCartTotal() {
        // Sử dụng CartService để tính tổng giá trị giỏ hàng
        BigDecimal total = cartService.calculateCartTotal(cartItems);
        totalLabel.setText(formatCurrency(total));
    }

    public void removeCartItem(int productId) {
        cartItems.removeIf(item -> item.getProduct().getId() == productId);
        cartItemCount.set(cartItems.size());
        updateCartTotal();
    }

    public ObservableList<CartItem> getCartItems() {
        return cartItems;
    }

    public void decreaseItemQuantity(int productId) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                item.decreaseQuantity();
                break;
            }
        }
        updateCartTotal();
    }

    public void increaseItemQuantity(int productId) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                item.increaseQuantity();
                // Đảm bảo UI cũng được cập nhật
                updateCartItemUI(productId, item.getQuantity());
                break;
            }
        }
        updateCartTotal();
    }

    private void updateCartItemUI(int productId, BigDecimal quantity) {
        for (Node node : cartItemsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox itemBox = (VBox) node;
                HBox topBox = (HBox) itemBox.getChildren().get(0);
                // Giả sử ta có thể lấy ID sản phẩm từ một thuộc tính userData của itemBox
                if (itemBox.getUserData() != null && itemBox.getUserData().equals(productId)) {
                    HBox quantityBox = (HBox) itemBox.getChildren().get(1);
                    TextField quantityField = (TextField) quantityBox.getChildren().get(1);
                    quantityField.setText(quantity.toPlainString());

                    // Cập nhật tổng tiền của item
                    Product product = null;
                    for (CartItem item : cartItems) {
                        if (item.getProduct().getId() == productId) {
                            product = item.getProduct();
                            break;
                        }
                    }
                    if (product != null) {
                        updateItemTotal(itemBox, product.getPrice(), quantity);
                    }
                    break;
                }
            }
        }
    }

    public void clearCart() {
        cartItemsContainer.getChildren().clear();
        cartItems.clear();
        cartItemCount.set(0);
        updateCartTotal();
    }
}