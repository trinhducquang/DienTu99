package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.enums.ProductStatus;
import org.example.quanlybanhang.model.Product;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Product> getAllProducts() {
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

    public Product getProductById(int productId) {
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

    public List<String> getAllCategoryNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM categories";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return names;
    }


    public void insertProduct(Product product) throws SQLException {
        String getCategorySql = "SELECT id FROM categories WHERE name = ?";
        int categoryId = -1;

        try (PreparedStatement categoryStatement = connection.prepareStatement(getCategorySql)) {
            categoryStatement.setString(1, product.getCategoryName());
            try (ResultSet categoryResult = categoryStatement.executeQuery()) {
                if (categoryResult.next()) {
                    categoryId = categoryResult.getInt("id");
                } else {
                    throw new SQLException("Không tìm thấy danh mục: " + product.getCategoryName());
                }
            }
        }

        String sql = "INSERT INTO products (name, category_id, description, price, stock_quantity, status, created_at, updated_at, image_url, specifications) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getName());
            statement.setInt(2, categoryId);
            statement.setString(3, product.getDescription());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStockQuantity());
            ProductStatus status = (product.getStockQuantity() > 0) ? ProductStatus.CON_HANG : ProductStatus.HET_HANG;
            statement.setString(6, status.toString());
            statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(9, product.getImageUrl());
            statement.setString(10, product.getSpecifications());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    public void updateProduct(Product product) {
        if (product.getStatus() != ProductStatus.DA_HUY) {
            product.setStatus(product.getStockQuantity() > 0 ? ProductStatus.CON_HANG : ProductStatus.HET_HANG);
        }

        String sql = "UPDATE products SET name = ?, category_id = ?, description = ?, price = ?, stock_quantity = ?, status = ?, updated_at = ?, image_url = ?, specifications = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getCategoryId()); // 👈 thêm dòng này để cập nhật category_id
            statement.setString(3, product.getDescription());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStockQuantity());
            statement.setString(6, product.getStatus().toString());
            statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(8, product.getImageUrl());
            statement.setString(9, product.getSpecifications());
            statement.setInt(10, product.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Product extractProductFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String categoryName = resultSet.getString("category_name");
        int categoryId = resultSet.getInt("category_id");
        String description = resultSet.getString("description");
        double price = resultSet.getDouble("price");
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
}
