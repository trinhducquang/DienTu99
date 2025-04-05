package org.example.quanlybanhang.enums;

public enum UserRole {
    ADMIN("Admin"),
    NHAN_VIEN("Nhân Viên"),
    NHAN_VIEN_KHO("Nhân viên kho"),
    THU_NGAN("Nhân viên thu ngân"),
    BAN_HANG("Nhân viên bán hàng"),
    LOCK("Lock");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromString(String text) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(text)) {
                return role;
            }
        }
        return null;
    }
}
