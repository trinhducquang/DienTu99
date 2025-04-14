package org.example.quanlybanhang.dto.productDTO;

import org.example.quanlybanhang.model.Product;

import java.math.BigDecimal;

public class CartItem {
    private Product product;
    private BigDecimal quantity;

    public CartItem(Product product, BigDecimal quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        this.quantity = this.quantity.add(BigDecimal.ONE);
    }

    public void decreaseQuantity() {
        if (this.quantity.compareTo(BigDecimal.ONE) > 0) {
            this.quantity = this.quantity.subtract(BigDecimal.ONE);
        }
    }

    public BigDecimal getTotalPrice() {
        return product.getPrice().multiply(quantity);
    }
}
