package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private Connection connection;

    public CategoryDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Category category = extractCategoryFromResultSet(resultSet);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    private Category extractCategoryFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        int parentId = resultSet.getInt("parent_id");

        return new Category(id, name, description, parentId);
    }

    public List<Category> getChildCategories(int parentId) {
        List<Category> childCategories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE parent_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parentId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Category category = extractCategoryFromResultSet(resultSet);
                childCategories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return childCategories;
    }



}