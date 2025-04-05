package org.example.quanlybanhang.model;

import org.example.quanlybanhang.enums.OrderStatus;
import java.time.LocalDateTime;

public class Order {
    private int id;
    private int employeeId;
    private int customerId;
    private String customerName; // Thêm tên khách hàng
    private double totalPrice;
    private double shippingFee;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private String productNames;
    private String note;

    // Constructor
    public Order(int id, int employeeId, int customerId, String customerName, double totalPrice, double shippingFee,
                 LocalDateTime orderDate, OrderStatus status, String productNames, String note) {
        this.id = id;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.shippingFee = shippingFee;
        this.orderDate = orderDate;
        this.status = status;
        this.productNames = productNames;
        this.note = note;
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}