package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.dto.warehouseDTO.ImportedWarehouseDTO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
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
                    WarehouseDTO dto = new WarehouseDTO();

                    dto.setId(rs.getInt("id"));
                    dto.setProductId(rs.getInt("product_id"));
                    dto.setTransactionCode(rs.getString("transaction_code"));
                    dto.setProductName(rs.getString("product_name"));
                    dto.setCategoryName(rs.getString("category_name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setUnitPrice(rs.getBigDecimal("unit_price"));
                    dto.setType(WarehouseType.fromValue(rs.getString("type")));
                    dto.setNote(rs.getString("note"));
                    dto.setCreatedByName(rs.getString("created_by_name"));
                    dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

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
                    BigDecimal sellPrice = rs.getBigDecimal("sell_price");
                    LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

                    WarehouseDTO dto = new WarehouseDTO();
                    dto.setId(id);
                    dto.setProductId(productId);
                    dto.setSku(sku);
                    dto.setProductName(productName);
                    dto.setCategoryName(categoryName);
                    dto.setUnitPrice(unitPrice);
                    dto.setSellPrice(sellPrice);
                    dto.setUpdatedAt(updatedAt);

                    productList.add(dto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn danh sách tồn kho:");
            e.printStackTrace();
        }

        return productList;
    }

    public List<WarehouseDTO> getAllWarehouseCheck() {
        List<WarehouseDTO> warehouseCheckList = new ArrayList<>();
        String query = "SELECT * FROM warehouse_transaction_details WHERE type = 'Kiểm Kho'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {

            if (rs != null) {
                while (rs.next()) {
                    WarehouseDTO dto = new WarehouseDTO();

                    dto.setTransactionCode(rs.getString("transaction_code"));
                    dto.setProductName(rs.getString("product_name"));
                    dto.setCreatedByName(rs.getString("created_by_name"));
                    dto.setInventoryDate(rs.getTimestamp("inventory_check_date").toLocalDateTime());
                    dto.setInventoryNote(rs.getString("inventory_note"));
                    dto.setExcessQuantity(rs.getInt("excess_quantity"));
                    dto.setDeficientQuantity(rs.getInt("deficient_quantity"));
                    dto.setMissing(rs.getInt("missing"));

                    warehouseCheckList.add(dto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn dữ liệu kiểm kho:");
            e.printStackTrace();
        }

        return warehouseCheckList;
    }

    public boolean insertWarehouseImport(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        if (transaction == null || productList == null || productList.isEmpty()) {
            System.err.println("Transaction or product list is null/empty.");
            return false;
        }

        final String insertSQL = """
        INSERT INTO warehouse_transactions (
            transaction_code, created_by, created_at,
            product_id, quantity, unit_price, note
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(true); // Bạn có thể bật false nếu muốn rollback khi có lỗi

            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                for (WarehouseDTO product : productList) {
                    if (product.getQuantity() <= 0 || product.getProductId() <= 0 || product.getUnitPrice() == null) {
                        continue; // Bỏ qua sản phẩm không hợp lệ
                    }

                    stmt.setString(1, transaction.getTransactionCode());
                    stmt.setInt(2, transaction.getCreateById());
                    stmt.setTimestamp(3, Timestamp.valueOf(transaction.getCreatedAt()));
                    stmt.setInt(4, product.getProductId());
                    stmt.setInt(5, product.getQuantity());
                    stmt.setBigDecimal(6, product.getUnitPrice());
                    stmt.setString(7, transaction.getNote());

                    stmt.addBatch();
                }

                stmt.executeBatch();
                return true;

            } catch (SQLException e) {
                System.err.println("❌ Lỗi khi thêm phiếu nhập kho: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertWarehouseCheck(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        String insertSQL = """
        INSERT INTO warehouse_transactions (
            transaction_code,
            product_id,
            type,
            note,
            created_at,
            created_by,
            inventory_check_date,
            inventory_note,
            excess_quantity,
            deficient_quantity,
            missing,
            inventory_status
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            conn.setAutoCommit(false);

            for (WarehouseDTO product : productList) {
                stmt.setString(1, transaction.getTransactionCode());
                stmt.setInt(2, product.getProductId());
                stmt.setString(3, transaction.getType().getValue());
                stmt.setString(4, transaction.getNote());
                stmt.setTimestamp(5, Timestamp.valueOf(transaction.getCreatedAt()));
                stmt.setInt(6, transaction.getCreateById());
                stmt.setTimestamp(7, Timestamp.valueOf(transaction.getInventoryDate()));
                stmt.setString(8, transaction.getInventoryNote());
                stmt.setInt(9, product.getExcessQuantity());
                stmt.setInt(10, product.getDeficientQuantity());
                stmt.setInt(11, product.getMissing());
                stmt.setString(12, product.getInventoryStatus().getValue());

                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ImportedWarehouseDTO> getProductsImportedFromWarehouse() {
        List<ImportedWarehouseDTO> productList = new ArrayList<>();
        String query = """
        SELECT DISTINCT p.id, p.name, wt.transaction_code
        FROM products p
        JOIN warehouse_transactions wt ON wt.product_id = p.id
        WHERE wt.type = 'Nhập kho'
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String transactionCode = rs.getString("transaction_code");

                ImportedWarehouseDTO importWarehouse = new ImportedWarehouseDTO(id, name, transactionCode);
                productList.add(importWarehouse);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }


}
