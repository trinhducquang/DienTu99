package org.example.quanlybanhang.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.quanlybanhang.dao.CategoryDAO;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService() {
        Connection connection = DatabaseConnection.getConnection();
        this.categoryDAO = new CategoryDAO(connection);
    }

    public ObservableList<Category> getAllCategories() {
        List<Category> categories = categoryDAO.getAll();
        return FXCollections.observableArrayList(categories);
    }

    public boolean addCategory(Category category) {
        return categoryDAO.save(category);
    }

    public ObservableList<Category> getChildCategories(int parentId) {
        List<Category> children = categoryDAO.getChildCategories(parentId);
        return FXCollections.observableArrayList(children);
    }

    public boolean updateCategory(Category category) {
        return categoryDAO.update(category);
    }



}
