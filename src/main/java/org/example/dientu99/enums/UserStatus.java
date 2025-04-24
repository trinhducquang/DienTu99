package org.example.dientu99.enums;

public enum UserStatus {
    LOCK("Lock"),
    UNLOCK("Unlock");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserStatus fromString(String text) {
        for (UserStatus status : UserStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return null;
    }
}
