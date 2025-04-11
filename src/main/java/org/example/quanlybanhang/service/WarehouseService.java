package org.example.quanlybanhang.service;

import org.example.quanlybanhang.enums.WarehouseType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class WarehouseService {

    public String generateTransactionCode(WarehouseType type) {
        String prefix = switch (type) {
            case NHAP_KHO -> "NK";
            case XUAT_KHO -> "XK";
            case KIEM_KHO -> "KH";
        };
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return prefix + "-" + datePart + "-" + randomPart;
    }
}
