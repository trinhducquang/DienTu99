package org.example.quanlybanhang.dto.warehouseDTO;

import org.example.quanlybanhang.enums.InventoryStatus;
import org.example.quanlybanhang.enums.WarehouseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WarehouseDTO {
    private int id;
    private int productId;
    private String sku;
    private String transactionCode;
    private String productName;
    private BigDecimal sellPrice;
    private String categoryName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private WarehouseType type;
    private String note;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime inventoryDate;
    private String inventoryNote;
    private InventoryStatus inventoryStatus;
    private int createById;
    private int excessQuantity;
    private int deficientQuantity;
    private int missing;


    public WarehouseDTO() {
    }

    public WarehouseDTO(int id, int productId, String transactionCode, String productName, BigDecimal sellPrice, String categoryName,
                        int quantity, BigDecimal unitPrice, WarehouseType type,
                        String note, String createdByName, LocalDateTime createdAt, LocalDateTime updatedAt,
                        LocalDateTime inventoryDate, int createById ,String inventoryNote, InventoryStatus inventoryStatus) {
        this.id = id;
        this.productId = productId;
        this.transactionCode = transactionCode;
        this.productName = productName;
        this.sellPrice = sellPrice;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.type = type;
        this.note = note;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.inventoryDate = inventoryDate;
        this.createById = createById;
        this.inventoryNote = inventoryNote;
        this.inventoryStatus = inventoryStatus;
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

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCreateById() {
        return createById;
    }

    public void setCreateById(int createById) {
        this.createById = createById;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public LocalDateTime getInventoryDate() {
        return inventoryDate;
    }

    public void setInventoryDate(LocalDateTime inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    public String getInventoryNote() {
        return inventoryNote;
    }

    public void setInventoryNote(String inventoryNote) {
        this.inventoryNote = inventoryNote;
    }

    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public int getExcessQuantity() {
        return excessQuantity;
    }

    public void setExcessQuantity(int excessQuantity) {
        this.excessQuantity = excessQuantity;
    }

    public int getDeficientQuantity() {
        return deficientQuantity;
    }

    public void setDeficientQuantity(int deficientQuantity) {
        this.deficientQuantity = deficientQuantity;
    }

    public int getMissing() {
        return missing;
    }

    public void setMissing(int missing) {
        this.missing = missing;
    }


}
