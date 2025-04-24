package org.example.dientu99.service;

import org.example.dientu99.dao.WarehouseDAO;
import org.example.dientu99.dto.warehouseDTO.WarehouseDTO;
import org.example.dientu99.enums.WarehouseType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WarehouseService {

    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    public String generateTransactionCode(WarehouseType type) {
        String prefix = switch (type) {
            case NHAP_KHO -> "NK";
            case XUAT_KHO -> "XK";
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

}