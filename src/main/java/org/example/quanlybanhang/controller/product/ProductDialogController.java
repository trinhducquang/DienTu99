package org.example.quanlybanhang.controller.product;

import com.google.gson.Gson;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductDialogController {
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @FXML
    private TextField productNameField, priceField, stockQuantityField, descriptionField, imageUrlField;

    @FXML private TextField configMemoryField;
    @FXML private TextField cameraField;
    @FXML private TextField batteryField;
    @FXML private TextField featuresField;
    @FXML private TextField connectivityField;
    @FXML private TextField designMaterialsField;

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

            String categoryName = selectedCategory.getName();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stockQuantity = Integer.parseInt(stockQuantityField.getText());
            String imageUrl = imageUrlField.getText();

            // Tạo specifications dưới dạng JSON bằng Gson
            Gson gson = new Gson();
            Map<String, String> specs = new LinkedHashMap<>();
            specs.put("configMemory", configMemoryField.getText());
            specs.put("camera", cameraField.getText());
            specs.put("battery", batteryField.getText());
            specs.put("features", featuresField.getText());
            specs.put("connectivity", connectivityField.getText());
            specs.put("designMaterials", designMaterialsField.getText());
            String specifications = gson.toJson(specs);

            Product newProduct = new Product();


            newProduct.setName(name);
            newProduct.setCategoryName(categoryName);
            newProduct.setDescription(description);
            newProduct.setPrice(price);
            newProduct.setStockQuantity(stockQuantity);
            newProduct.setCreatedAt(LocalDateTime.now());
            newProduct.setUpdatedAt(LocalDateTime.now());
            newProduct.setImageUrl(imageUrl);
            newProduct.setSpecifications(specifications);

            productDAO.save(newProduct);
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