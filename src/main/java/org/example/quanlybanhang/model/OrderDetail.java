package org.example.quanlybanhang.model;

public class OrderDetail {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private double price;
    private double total;

    public OrderDetail(int id, int orderId, int productId, int quantity, double price) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.total = quantity * price; // ✅ Tính thành tiền tự động
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.total = this.quantity * this.price;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.price = price;
        this.total = this.quantity * this.price;
    }

    public double getTotal() { return total; }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", total=" + total +
                '}';
    }

}
