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