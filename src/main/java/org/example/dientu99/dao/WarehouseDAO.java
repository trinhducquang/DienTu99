package org.example.dientu99.dao;

import org.example.dientu99.dto.warehouseDTO.WarehouseDTO;
import org.example.dientu99.enums.ProductStatus;
import org.example.dientu99.enums.WarehouseType;
import org.example.dientu99.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                    String productName = rs.getString("product_name");
                    String categoryName = rs.getString("category_name");
                    BigDecimal unitPrice = rs.getBigDecimal("unit_price");
                    BigDecimal sellPrice = rs.getBigDecimal("sell_price");

                    LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

                    WarehouseDTO dto = new WarehouseDTO();
                    dto.setId(id);
                    dto.setProductId(productId);
                    dto.setProductName(productName);
                    dto.setCategoryName(categoryName);
                    dto.setUnitPrice(unitPrice);
                    dto.setSellPrice(sellPrice);
//                    System.out.println("product ID: " + productId + ",sellprice: " + dto.getSellPrice());
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


    public boolean insertWarehouseImport(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        if (transaction == null || productList == null || productList.isEmpty()) {
            System.err.println("Transaction or product list is null/empty.");
            return false;
        }

        final String insertSQL = """
                    INSERT INTO warehouse_transactions (
                        transaction_code, created_by, created_at,
                        product_id, quantity, unit_price, note, type
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;

            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                for (WarehouseDTO product : productList) {
                    if (product.getQuantity() <= 0 || product.getProductId() <= 0 || product.getUnitPrice() == null) {
                        continue;
                    }

                    stmt.setString(1, transaction.getTransactionCode());
                    stmt.setInt(2, transaction.getCreateById());
                    stmt.setTimestamp(3, Timestamp.valueOf(transaction.getCreatedAt()));
                    stmt.setInt(4, product.getProductId());
                    stmt.setInt(5, product.getQuantity());
                    stmt.setBigDecimal(6, product.getUnitPrice());
                    stmt.setString(7, transaction.getNote());
                    stmt.setString(8, transaction.getType().getValue());
                    stmt.addBatch();
                    updateProductStock(conn, product.getProductId(), product.getQuantity());
                }

                stmt.executeBatch();
                conn.commit(); // Commit the transaction
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                System.err.println("❌ Error adding import transaction: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


    private void updateProductStock(Connection conn, int productId, int quantityChange) throws SQLException {
        try {
            String updateStockSQL = "UPDATE products SET stock_quantity = stock_quantity + ?, updated_at = NOW() WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateStockSQL)) {
                pstmt.setInt(1, quantityChange);
                pstmt.setInt(2, productId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Không cập nhật được tồn kho, ID sản phẩm có thể không tồn tại: " + productId);
                }
            }

            int newQuantity = 0;
            String selectStockSQL = "SELECT stock_quantity FROM products WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectStockSQL)) {
                pstmt.setInt(1, productId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        newQuantity = rs.getInt("stock_quantity");
                    } else {
                        throw new SQLException("Không tìm thấy sản phẩm với ID: " + productId);
                    }
                }
            }

            ProductStatus status = (newQuantity > 0)
                    ? ProductStatus.CON_HANG
                    : ProductStatus.HET_HANG;

            String updateStatusSQL = "UPDATE products SET status = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateStatusSQL)) {
                pstmt.setString(1, status.getValue()); // Ví dụ: "Còn hàng"
                pstmt.setInt(2, productId);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw e;
        }
    }

    public List<WarehouseDTO> getProductImportedByDateAsc(int productId) {
        List<WarehouseDTO> imports = new ArrayList<>();
        String query = """
        SELECT wt.id, wt.product_id, wt.quantity, wt.transaction_code, 
               wt.created_at, p.name as product_name 
        FROM warehouse_transactions wt
        JOIN products p ON wt.product_id = p.id
        WHERE wt.product_id = ? AND wt.type = 'Nhập Kho'
        ORDER BY wt.created_at ASC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WarehouseDTO dto = new WarehouseDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setProductId(rs.getInt("product_id"));
                    dto.setTransactionCode(rs.getString("transaction_code"));
                    dto.setProductName(rs.getString("product_name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    imports.add(dto);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn danh sách nhập kho theo ngày tạo:");
            e.printStackTrace();
        }
        return imports;
    }

    public boolean insertWarehouseExportWithFIFO(WarehouseDTO transaction, List<WarehouseDTO> productList) {
        if (transaction == null || productList == null || productList.isEmpty()) {
            System.err.println("Transaction or product list is null/empty.");
            return false;
        }

        final String insertSQL = """
        INSERT INTO warehouse_transactions (
            transaction_code, created_by, created_at,
            product_id, quantity, unit_price, type, note,
            source_transaction_code
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("❌ Không thể kết nối đến cơ sở dữ liệu.");
                return false;
            }

            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                for (WarehouseDTO product : productList) {
                    if (product.getQuantity() <= 0 || product.getProductId() <= 0) {
                        continue;
                    }
                    List<WarehouseDTO> importBatches = getProductImportedByDateAsc(product.getProductId());
                    int remainingQuantity = product.getQuantity();

                    for (WarehouseDTO batch : importBatches) {
                        if (remainingQuantity <= 0) break;

                        int exportQuantity = Math.min(remainingQuantity, batch.getQuantity());
                        remainingQuantity -= exportQuantity;

                        stmt.setString(1, transaction.getTransactionCode());
                        stmt.setInt(2, transaction.getCreateById());
                        stmt.setTimestamp(3, Timestamp.valueOf(transaction.getCreatedAt()));
                        stmt.setInt(4, product.getProductId());
                        stmt.setInt(5, exportQuantity);
                        stmt.setBigDecimal(6, BigDecimal.ZERO);
                        stmt.setString(7, transaction.getType().getValue());
                        stmt.setString(8, transaction.getNote());
                        stmt.setString(9, batch.getTransactionCode());
                        stmt.addBatch();
                    }
                    updateProductStock(conn, product.getProductId(), -product.getQuantity());
                }

                stmt.executeBatch();
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("❌ Lỗi khi thêm phiếu xuất kho FIFO: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


}
