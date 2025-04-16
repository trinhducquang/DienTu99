package org.example.quanlybanhang.model;

import org.example.quanlybanhang.enums.WarehouseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Warehouse {
    private int id;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private WarehouseType type;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String transactionCode;
    private int createdBy;
    private int excessQuantity;
    private int missing;
    private int deficientQuantity;


    public Warehouse() {
    }

    public Warehouse(int id, int productId, int quantity, BigDecimal unitPrice, WarehouseType type,
                     String note, LocalDateTime createdAt, LocalDateTime updatedAt, String transactionCode,
                     int createdBy, int excessQuantity, int missing, int deficientQuantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.type = type;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.transactionCode = transactionCode;
        this.createdBy = createdBy;
        this.excessQuantity = excessQuantity;
        this.missing = missing;
        this.deficientQuantity = deficientQuantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getExcessQuantity() {
        return excessQuantity;
    }

    public void setExcessQuantity(int excessQuantity) {
        this.excessQuantity = excessQuantity;
    }

    public int getMissing() {
        return missing;
    }

    public void setMissing(int missing) {
        this.missing = missing;
    }

    public int getDeficientQuantity() {
        return deficientQuantity;
    }

    public void setDeficientQuantity(int deficientQuantity) {
        this.deficientQuantity = deficientQuantity;
    }
}
