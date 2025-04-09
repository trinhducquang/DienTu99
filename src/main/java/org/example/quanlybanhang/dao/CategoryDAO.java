package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.dao.base.CrudDao;
import org.example.quanlybanhang.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO implements CrudDao<Category> {
    private Connection connection;

    public CategoryDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean save(Category category) {
        String sql = "INSERT INTO categories (name, description, parent_id) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            if (category.getParentId() > 0) {
                statement.setInt(3, category.getParentId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, description = ?, parent_id = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            if (category.getParentId() > 0) {
                statement.setInt(3, category.getParentId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }
            statement.setInt(4, category.getId());

            int rowsUpdated = statement.executeUpdate(); // <- lấy số dòng bị ảnh hưởng
            return rowsUpdated > 0; // thành công nếu có ít nhất 1 dòng
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void delete(Category category) {
        System.out.println("chưa có gì");
    }

    @Override
    public Category findById(int id) {
        String sql = "SELECT id, name, description, parent_id, " +
                "CASE WHEN parent_id IS NULL THEN 'Danh mục cha' ELSE 'Danh mục con' END AS category " +
                "FROM categories WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extractCategoryFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name, description, parent_id, " +
                "CASE WHEN parent_id IS NULL THEN 'Danh mục cha' ELSE 'Danh mục con' END AS category " +
                "FROM categories";

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

    public List<Category> getChildCategories(int parentId) {
        List<Category> childCategories = new ArrayList<>();
        String sql = "SELECT id, name, description, parent_id, " +
                "CASE WHEN parent_id IS NULL THEN 'Danh mục cha' ELSE 'Danh mục con' END AS category " +
                "FROM categories WHERE parent_id = ?";

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

    private Category extractCategoryFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        int parentId = resultSet.getInt("parent_id");
        String categoryType = resultSet.getString("category");

        return new Category(id, name, description, parentId, categoryType);
    }
}
