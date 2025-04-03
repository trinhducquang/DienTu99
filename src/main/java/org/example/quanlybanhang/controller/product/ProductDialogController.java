package org.example.quanlybanhang.controller.product;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.quanlybanhang.dao.CategoryDAO;
import org.example.quanlybanhang.dao.ProductDAO;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDialogController {
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @FXML
    private TextField productNameField, priceField, stockQuantityField, descriptionField, imageUrlField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private Button saveButton, cancelButton;

    public ProductDialogController() {
        Connection connection = DatabaseConnection.getConnection();
        this.productDAO = new ProductDAO(connection);
        this.categoryDAO = new CategoryDAO(connection);
    }

    @FXML
    public void initialize() {
        loadCategories(); // Tải danh mục vào ComboBox

        // Thêm các sự kiện cho nút
        saveButton.setOnAction(event -> saveProduct());
        cancelButton.setOnAction(event -> closeDialog());
    }

    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        categoryComboBox.getItems().addAll(categories); // Thêm đối tượng Category vào ComboBox

        // Thiết lập cách hiển thị danh mục trong ComboBox
        categoryComboBox.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return (category != null) ? category.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });
    }

    private void saveProduct() {
        try {
            String name = productNameField.getText();
            Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();

            if (selectedCategory == null) {
                System.out.println("Vui lòng chọn danh mục!");
                return;
            }

            String categoryName = selectedCategory.getName(); // Giờ không bị lỗi nữa
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stockQuantity = Integer.parseInt(stockQuantityField.getText());
            String imageUrl = imageUrlField.getText();

            Product newProduct = new Product(0, name, categoryName, description, price, stockQuantity, LocalDateTime.now(), LocalDateTime.now(), null, imageUrl, null);
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