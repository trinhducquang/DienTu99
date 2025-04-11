package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.InventoryStatus;
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
        String query = "SELECT * FROM warehouse_transaction_details";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {

            if (rs != null) {
                while (rs.next()) {
                    WarehouseDTO dto = new WarehouseDTO();

                    dto.setTransactionCode(rs.getString("transaction_code"));
                    dto.setProductName(rs.getString("product_name"));
                    dto.setExcessQuantity(rs.getInt("excess_quantity"));
                    dto.setCreatedByName(rs.getString("created_by_name"));
                    dto.setInventoryDate(rs.getTimestamp("inventory_check_date").toLocalDateTime());
                    dto.setInventoryNote(rs.getString("inventory_note"));
                    dto.setInventoryStatus(InventoryStatus.fromValue(rs.getString("inventory_status")));
                    dto.setExcessQuantity(rs.getInt("excess_quantity"));
                    dto.setDeficientQuantity(rs.getInt("deficient_quantity"));
                    dto.setMissing(rs.getInt("missing"));


                    warehouseCheckList.add(dto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn dữ liệu warehouse_transaction_check:");
            e.printStackTrace();
        }

        return warehouseCheckList;
    }

    public boolean insertWarehouseTransaction(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        String insertTransactionSQL =
                "INSERT INTO warehouse_transaction (" +
                        "transaction_code, created_by, transaction_type, created_at, " +
                        "total_amount, excess_quantity, deficient_quantity, note" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String insertDetailSQL =
                "INSERT INTO warehouse_transaction_details (" +
                        "transaction_code, product_id, quantity, unit_price" +
                        ") VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try (
                    PreparedStatement transactionStmt = conn.prepareStatement(insertTransactionSQL);
                    PreparedStatement detailStmt = conn.prepareStatement(insertDetailSQL)
            ) {
                // Insert main transaction
                transactionStmt.setString(1, transaction.getTransactionCode());
                transactionStmt.setString(2, transaction.getCreatedByName());
                transactionStmt.setString(3, transaction.getType().getValue());
                transactionStmt.setTimestamp(4, Timestamp.valueOf(transaction.getCreatedAt()));
                transactionStmt.setBigDecimal(5, transaction.getTotalAmount());
                transactionStmt.setInt(6, transaction.getExcessQuantity());
                transactionStmt.setInt(7, transaction.getDeficientQuantity());
                transactionStmt.setString(8, transaction.getNote());

                transactionStmt.executeUpdate();

                // Insert product details
                for (WarehouseDTO product : productList) {
                    detailStmt.setString(1, transaction.getTransactionCode());
                    detailStmt.setInt(2, product.getProductId());
                    detailStmt.setInt(3, product.getQuantity());
                    detailStmt.setBigDecimal(4, product.getUnitPrice());

                    detailStmt.addBatch();
                }

                detailStmt.executeBatch();
                conn.commit();

                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi khi thêm phiếu nhập kho:");
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



}
