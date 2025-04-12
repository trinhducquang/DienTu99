package org.example.quanlybanhang.enums;

public enum UserRole {
    ADMIN("Admin"),
    NHAN_VIEN("Nhân Viên"),
    NHAN_VIEN_KHO("Nhân viên kho"),
    THU_NGAN("Nhân viên thu ngân"),
    BAN_HANG("Nhân viên bán hàng");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // Hiển thị đẹp trong UI như ComboBox, TableView, v.v.
    @Override
    public String toString() {
        return value;
    }

    // Tìm UserRole từ value (hiển thị)
    public static UserRole fromString(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        return null;
    }
}
