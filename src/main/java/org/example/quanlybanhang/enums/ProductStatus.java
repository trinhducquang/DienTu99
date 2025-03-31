package org.example.quanlybanhang.enums;

public enum ProductStatus {
    CON_HANG("Còn hàng"),
    HET_HANG("Hết hàng"),
    DANG_CHO("Đang chờ"),
    DA_HUY("Ngừng Bán");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ProductStatus fromString(String text) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + text);
    }
}
