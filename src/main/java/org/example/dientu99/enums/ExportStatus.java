package org.example.dientu99.enums;

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
        if (value == null || value.trim().isEmpty()) {
            return CHUA_XUAT_KHO;
        }

        value = value.trim();
        for (ExportStatus status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }

        return CHUA_XUAT_KHO;
    }

    @Override
    public String toString() {
        return value;
    }
}
