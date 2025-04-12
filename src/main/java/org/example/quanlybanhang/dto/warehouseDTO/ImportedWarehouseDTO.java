package org.example.quanlybanhang.dto.warehouseDTO;

public class ImportedWarehouseDTO {
    private int id;
    private String name;
    private String transactionCode;

    public ImportedWarehouseDTO(int id, String name, String transactionCode) {
        this.id = id;
        this.name = name;
        this.transactionCode = transactionCode;
    }

    // Getters và Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTransactionCode() { return transactionCode; }

    @Override
    public String toString() {
        return name; // dùng cho ComboBox hiển thị tên sản phẩm
    }
}
