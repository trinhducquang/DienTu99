package org.example.quanlybanhang.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.CategoryDAO;
import org.example.quanlybanhang.dao.ProductDAO;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.database.DatabaseConnection;

import java.sql.Connection;
import java.util.List;

public class ProductDialogController {
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @FXML
    private TextField productIdField, productNameField, priceField, stockQuantityField, descriptionField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private Button saveButton, cancelButton;

    public ProductDialogController() {
        Connection connection = DatabaseConnection.getConnection();
        this.productDAO = new ProductDAO(connection);
        this.categoryDAO = new CategoryDAO(connection);
    }

    @FXML
    public void initialize() {
        // Tải danh mục vào ComboBox
        loadCategories();

        // Thêm các sự kiện cho nút
        saveButton.setOnAction(event -> saveProduct());
        cancelButton.setOnAction(event -> closeDialog());
    }

    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category category : categories) {
            categoryComboBox.getItems().add(category.getName()); // Chỉ hiển thị tên danh mục
        }
    }

    private void saveProduct() {
        try {
            String name = productNameField.getText();
            int categoryId = categoryComboBox.getSelectionModel().getSelectedIndex() + 1; // Giả sử category_id bắt đầu từ 1
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stockQuantity = Integer.parseInt(stockQuantityField.getText());

            Product newProduct = new Product(0, name, categoryId, description, price, stockQuantity, null, null);
            productDAO.insertProduct(newProduct);

            closeDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
