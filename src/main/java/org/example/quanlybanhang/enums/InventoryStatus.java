package org.example.quanlybanhang.enums;

public enum InventoryStatus {
    DA_XAC_NHAN("Đã xác nhận"),
    CO_CHENH_LECH("Có chênh lệch"),
    CHO_XAC_NHAN("Chờ xác nhận");

    private final String value;

    InventoryStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static InventoryStatus fromValue(String value) {
        for (InventoryStatus status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Trạng thái kiểm kho không hợp lệ: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
