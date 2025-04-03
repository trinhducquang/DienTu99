package org.example.quanlybanhang.enums;

public enum OrderStatus {
    DANG_XU_LY("Đang xử lý"),
    HOAN_THANH("Hoàn thành"),
    DA_HUY("Đã hủy");

    private final String text;

    OrderStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static OrderStatus fromString(String text) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.text.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + text);
    }
}
