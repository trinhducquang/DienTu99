package org.example.dientu99.dto.productDTO;



import org.example.dientu99.model.Product;

import java.math.BigDecimal;

public class CartItem {

    private final Product product;
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

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity() {
        quantity = quantity.add(BigDecimal.ONE);
    }

    public void decreaseQuantity() {
        if (quantity.compareTo(BigDecimal.ONE) > 0) {
            quantity = quantity.subtract(BigDecimal.ONE);
        }
    }

    public BigDecimal getTotal() {
        return product.getPrice().multiply(quantity);
    }

}