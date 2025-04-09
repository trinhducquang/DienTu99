package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.dto.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WarehouseDAO {

    public List<WarehouseDTO> getAllWarehouseDetails() {
        List<WarehouseDTO> warehouseDTOList = new ArrayList<>();
        String query = "SELECT * FROM warehouse_transaction_details";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {

            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int productId = rs.getInt("product_id");
                    String transactionCode = rs.getString("transaction_code");
                    String productName = rs.getString("product_name");
                    String categoryName = rs.getString("category_name");
                    int quantity = rs.getInt("quantity");
                    BigDecimal unitPrice = rs.getBigDecimal("unit_price");
                    WarehouseType type = WarehouseType.fromValue(rs.getString("type"));
                    String note = rs.getString("note");
                    String createdByName = rs.getString("created_by_name");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                    // Truyền 'type' vào constructor của WarehouseDTO
                    WarehouseDTO dto = new WarehouseDTO(
                            id,
                            productId,
                            transactionCode,
                            productName,
                            categoryName,
                            quantity,
                            unitPrice,
                            type,
                            note,
                            createdByName,
                            createdAt
                    );

                    warehouseDTOList.add(dto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn dữ liệu warehouse_transaction_details:");
            e.printStackTrace();
        }

        return warehouseDTOList;
    }

    public List<WarehouseDTO> getAllWarehouseProducts() {
        List<WarehouseDTO> productList = new ArrayList<>();
        String query = "SELECT * FROM warehouse_transaction_details"; // Hoặc SELECT DISTINCT nếu cần

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {

            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int productId = rs.getInt("product_id");
                    String sku = rs.getString("sku");
                    String productName = rs.getString("product_name");
                    String categoryName = rs.getString("category_name");
//                  int quantity = rs.getInt("stock_quality"); // Đây là tồn kho
                    BigDecimal unitPrice = rs.getBigDecimal("unit_price");
//                    BigDecimal sellPrice = rs.getBigDecimal("sell_price");
//                    LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

                    WarehouseDTO dto = new WarehouseDTO();
                    dto.setId(id);
                    dto.setProductId(productId);
                    dto.setSku(sku);
                    dto.setProductName(productName);
                    dto.setCategoryName(categoryName);
//                    dto.setQuantity(quantity);
                    dto.setUnitPrice(unitPrice);
//                    dto.setTotalAmount(sellPrice);
//                    dto.setCreatedAt(updatedAt);

                    productList.add(dto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn danh sách tồn kho:");
            e.printStackTrace();
        }

        return productList;
    }

}
