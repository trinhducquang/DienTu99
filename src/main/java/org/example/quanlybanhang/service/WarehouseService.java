package org.example.quanlybanhang.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class WarehouseService {

    public String generateTransactionCode() {
        String prefix = "NK";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return prefix + "-" + datePart + "-" + randomPart;
    }

}
