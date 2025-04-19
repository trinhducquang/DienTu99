package org.example.quanlybanhang.service;

import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.ImportedWarehouseDTO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // Triển khai FIFO cho xuất kho
    public boolean insertWarehouseExport(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        // Lấy danh sách các sản phẩm đã nhập kho để áp dụng FIFO
        List<ImportedWarehouseDTO> importedProducts = warehouseDAO.getProductsImportedFromWarehouse();

        // Tạo bản sao của danh sách sản phẩm cần xuất kho để không làm thay đổi tham số đầu vào
        List<WarehouseDTO> fifoProductList = new ArrayList<>();

        // Áp dụng FIFO cho từng sản phẩm trong danh sách xuất kho
        for (WarehouseDTO product : productList) {
            // Tìm tất cả các lần nhập kho của sản phẩm cần xuất
            List<ImportedWarehouseDTO> productImports = importedProducts.stream()
                    .filter(imp -> imp.getId() == product.getProductId())
                    .toList();

            // Sắp xếp các lần nhập kho theo mã giao dịch để lấy các lần nhập trước tiên
            // Giả định rằng mã giao dịch có chứa ngày tháng để sắp xếp theo thời gian
            List<ImportedWarehouseDTO> sortedImports = new ArrayList<>(productImports);
            Collections.sort(sortedImports, Comparator.comparing(ImportedWarehouseDTO::getTransactionCode));

            // Nếu không có lần nhập kho nào, thêm sản phẩm vào danh sách xuất kho như bình thường
            if (sortedImports.isEmpty()) {
                fifoProductList.add(product);
                continue;
            }

            // Áp dụng FIFO để xuất kho từ các lần nhập cũ nhất
            int remainingQuantity = product.getQuantity();
            for (ImportedWarehouseDTO importedProduct : sortedImports) {
                if (remainingQuantity <= 0) break;

                // Lấy thông tin chi tiết về lần nhập kho
                List<WarehouseDTO> importDetails = getImportDetails(importedProduct.getTransactionCode());

                // Tìm thông tin chi tiết về sản phẩm cụ thể trong lần nhập kho
                Optional<WarehouseDTO> productDetail = importDetails.stream()
                        .filter(detail -> detail.getProductId() == product.getProductId())
                        .findFirst();

                if (productDetail.isPresent()) {
                    // Tạo một bản sao của sản phẩm cần xuất để áp dụng FIFO
                    WarehouseDTO fifoProduct = new WarehouseDTO();
                    fifoProduct.setProductId(product.getProductId());
                    fifoProduct.setProductName(product.getProductName());
                    fifoProduct.setCategoryName(product.getCategoryName());

                    // Lấy số lượng cần xuất từ lần nhập kho này
                    int quantityFromThisImport = Math.min(remainingQuantity, productDetail.get().getQuantity());
                    fifoProduct.setQuantity(quantityFromThisImport);

                    // Sử dụng giá nhập kho từ lần nhập kho này
                    fifoProduct.setUnitPrice(productDetail.get().getUnitPrice());

                    // Cập nhật số lượng còn lại cần xuất
                    remainingQuantity -= quantityFromThisImport;

                    // Thêm vào danh sách sản phẩm xuất kho theo FIFO
                    fifoProductList.add(fifoProduct);
                }
            }

            // Nếu vẫn còn số lượng cần xuất mà không có đủ thông tin nhập kho
            if (remainingQuantity > 0) {
                WarehouseDTO remainingProduct = new WarehouseDTO();
                remainingProduct.setProductId(product.getProductId());
                remainingProduct.setProductName(product.getProductName());
                remainingProduct.setCategoryName(product.getCategoryName());
                remainingProduct.setQuantity(remainingQuantity);
                remainingProduct.setUnitPrice(BigDecimal.ZERO);
                fifoProductList.add(remainingProduct);
            }
        }

        // Gọi hàm xuất kho với danh sách đã được áp dụng FIFO
        return warehouseDAO.insertWarehouseExport(transaction, fifoProductList);
    }

    // Phương thức này sẽ lấy chi tiết của một lần nhập kho dựa trên mã giao dịch
    private List<WarehouseDTO> getImportDetails(String transactionCode) {
        return warehouseDAO.getImportDetailsByTransactionCode(transactionCode);
    }

    public boolean insertWarehouseCheck(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        return warehouseDAO.insertWarehouseCheck(transaction, productList);
    }
}