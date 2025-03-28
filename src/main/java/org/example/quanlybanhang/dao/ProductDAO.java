package org.example.quanlybanhang.dao;

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
        String sql = "SELECT * FROM products";

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

    public void insertProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, category_id, description, price, stock_quantity, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getCategoryId());
            statement.setString(3, product.getDescription());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStockQuantity());
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

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


    private Product extractProductFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        int categoryId = resultSet.getInt("category_id");
        String description = resultSet.getString("description");
        double price = resultSet.getDouble("price");
        int stockQuantity = resultSet.getInt("stock_quantity");
        Timestamp createdAtTimestamp = resultSet.getTimestamp("created_at");
        Timestamp updatedAtTimestamp = resultSet.getTimestamp("updated_at");

        LocalDateTime createdAt = createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : null;
        LocalDateTime updatedAt = updatedAtTimestamp != null ? updatedAtTimestamp.toLocalDateTime() : null;

        return new Product(id, name, categoryId, description, price, stockQuantity, createdAt, updatedAt);
    }
}