package org.example.quanlybanhang.dto;

import org.example.quanlybanhang.enums.WarehouseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WarehouseDTO {
    private int id;
    private int productId;
    private String transactionCode;
    private String productName;
    private String categoryName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private WarehouseType type; // Đổi từ String sang Enum
    private String note;
    private String createdByName;
    private LocalDateTime createdAt;

    public WarehouseDTO() {
    }

    public WarehouseDTO(int id, int productId, String transactionCode, String productName, String categoryName,
                        int quantity, BigDecimal unitPrice, WarehouseType type, // Enum ở đây
                        String note, String createdByName, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.transactionCode = transactionCode;
        this.productName = productName;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.type = type;
        this.note = note;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
    }

    // Getters & Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateTotalAmount();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        updateTotalAmount();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    private void updateTotalAmount() {
        if (this.unitPrice != null) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public WarehouseType getType() {
        return type;
    }

    public void setType(WarehouseType type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
