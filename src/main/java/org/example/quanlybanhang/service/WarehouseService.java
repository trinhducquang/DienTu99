package org.example.quanlybanhang.service;

import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WarehouseService {

    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

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

    public boolean insertWarehouseImport(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        return warehouseDAO.insertWarehouseImport(transaction, productList);
    }

    public boolean insertWarehouseExport(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        return warehouseDAO.insertWarehouseExportWithFIFO(transaction, productList);
    }

    public boolean insertWarehouseCheck(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        return warehouseDAO.insertWarehouseCheck(transaction, productList);
    }
}