package org.example.quanlybanhang.dto;

import org.example.quanlybanhang.enums.OrderStatus;

import java.time.LocalDateTime;

public class OrderSummaryDTO {
    private int id;
    private int employeeId;
    private int customerId;
    private String productIds;
    private String customerName;
    private double totalPrice;
    private double shippingFee;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private String productNames;
    private String productImages;
    private String productQuantities;
    private String productPrices;
    private String note;

    public OrderSummaryDTO() {
    }

    public OrderSummaryDTO(int id, String productIds, int employeeId, int customerId, String customerName,
                           double totalPrice, double shippingFee, LocalDateTime orderDate,
                           OrderStatus status, String productNames, String productImages,
                           String productQuantities, String productPrices, String note) {
        this.id = id;
        this.productIds = productIds;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.shippingFee = shippingFee;
        this.orderDate = orderDate;
        this.status = status;
        this.productNames = productNames;
        this.productImages = productImages;
        this.productQuantities = productQuantities;
        this.productPrices = productPrices;
        this.note = note;
    }


    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductIds() {
        return productIds;
    }

    public void setProductIds(String productIds) {
        this.productIds = productIds;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getProductImages() {
        return productImages;
    }

    public void setProductImages(String productImages) {
        this.productImages = productImages;
    }

    public String getProductQuantities() {
        return productQuantities;
    }

    public void setProductQuantities(String productQuantities) {
        this.productQuantities = productQuantities;
    }

    public String getProductPrices() {
        return productPrices;
    }

    public void setProductPrices(String productPrices) {
        this.productPrices = productPrices;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
