package org.example.dientu99.dao;

import org.example.dientu99.dao.base.CrudDao;
import org.example.dientu99.enums.ProductStatus;
import org.example.dientu99.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements CrudDao<Product> {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name FROM products p JOIN categories c ON p.category_id = c.id";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Product product = extractProductFromResultSet(resultSet);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public Product findById(int productId) {
        String sql = "SELECT p.*, c.name AS category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE p.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractProductFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(Product product) {
        String getCategorySql = "SELECT id FROM categories WHERE name = ?";
        int categoryId;

        try (PreparedStatement categoryStmt = connection.prepareStatement(getCategorySql)) {
            categoryStmt.setString(1, product.getCategoryName());
            try (ResultSet categoryResult = categoryStmt.executeQuery()) {
                if (categoryResult.next()) {
                    categoryId = categoryResult.getInt("id");
                } else {
                    throw new SQLException("Không tìm thấy danh mục: " + product.getCategoryName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO products (name, category_id, description, price, stock_quantity, status, created_at, updated_at, image_url, specifications) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, categoryId);
            stmt.setString(3, product.getDescription());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setInt(5, product.getStockQuantity());

            ProductStatus status = product.getStockQuantity() > 0 ? ProductStatus.CON_HANG : ProductStatus.HET_HANG;
            stmt.setString(6, status.toString());

            LocalDateTime now = LocalDateTime.now();
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            stmt.setTimestamp(8, Timestamp.valueOf(now));
            stmt.setString(9, product.getImageUrl());
            stmt.setString(10, product.getSpecifications());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setId(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Product product) {
        if (product.getStatus() != ProductStatus.DA_HUY) {
            product.setStatus(product.getStockQuantity() > 0 ? ProductStatus.CON_HANG : ProductStatus.HET_HANG);
        }

        String sql = "UPDATE products SET name = ?, category_id = ?, description = ?, price = ?, stock_quantity = ?, status = ?, updated_at = ?, image_url = ?, specifications = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getCategoryId());
            statement.setString(3, product.getDescription());
            statement.setBigDecimal(4, product.getPrice());
            statement.setInt(5, product.getStockQuantity());
            statement.setString(6, product.getStatus().toString());
            statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(8, product.getImageUrl());
            statement.setString(9, product.getSpecifications());
            statement.setInt(10, product.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Product extractProductFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String categoryName = resultSet.getString("category_name");
        int categoryId = resultSet.getInt("category_id");
        String description = resultSet.getString("description");
        BigDecimal price = resultSet.getBigDecimal("price");
        int stockQuantity = resultSet.getInt("stock_quantity");
        Timestamp createdAtTimestamp = resultSet.getTimestamp("created_at");
        Timestamp updatedAtTimestamp = resultSet.getTimestamp("updated_at");
        String statusText = resultSet.getString("status");
        String imageUrl = resultSet.getString("image_url");
        String specifications = resultSet.getString("specifications");

        LocalDateTime createdAt = (createdAtTimestamp != null) ? createdAtTimestamp.toLocalDateTime() : null;
        LocalDateTime updatedAt = (updatedAtTimestamp != null) ? updatedAtTimestamp.toLocalDateTime() : null;
        ProductStatus status = ProductStatus.fromString(statusText);

        return new Product(id, name, categoryName, categoryId, description, price, stockQuantity, createdAt, updatedAt, status, imageUrl, specifications);
    }

    public List<Product> getRelatedProducts(int categoryId, int excludedProductId, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, price, image_url FROM products WHERE category_id = ? AND id != ? LIMIT ? OFFSET ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.setInt(2, excludedProductId);
            stmt.setInt(3, limit);
            stmt.setInt(4, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setImageUrl(rs.getString("image_url"));
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public int countRelatedProducts(int categoryId, int excludedProductId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ? AND id != ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.setInt(2, excludedProductId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }



}
