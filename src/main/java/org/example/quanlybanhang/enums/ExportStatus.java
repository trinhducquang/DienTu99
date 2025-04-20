package org.example.quanlybanhang.enums;

public enum ExportStatus {
    DA_XUAT_KHO("Đã xuất kho"),
    CHUA_XUAT_KHO("Chưa xuất kho");

    private final String value;

    ExportStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExportStatus fromValue(String value) {
        for (ExportStatus status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Giá trị trạng thái xuất kho không hợp lệ: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
