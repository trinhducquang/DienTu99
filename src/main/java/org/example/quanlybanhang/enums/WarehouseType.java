package org.example.quanlybanhang.enums;

public enum WarehouseType {
    NHAP_KHO("Nhập Kho"),
    XUAT_KHO("Xuất Kho");

    private final String value;

    WarehouseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static WarehouseType fromValue(String value) {
        for (WarehouseType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Không hợp lệ: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
