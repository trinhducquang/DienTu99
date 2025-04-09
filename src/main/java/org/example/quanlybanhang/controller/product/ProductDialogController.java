package org.example.quanlybanhang.controller.product;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.quanlybanhang.model.*;
import org.example.quanlybanhang.service.*;

import java.time.LocalDateTime;
import java.util.*;

public class ProductDialogController {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @FXML
    private TextField productNameField, priceField, stockQuantityField, descriptionField, imageUrlField;
    @FXML private TextField configMemoryField, cameraField, batteryField, featuresField, connectivityField, designMaterialsField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private Button saveButton, cancelButton;

    @FXML
    public void initialize() {
        loadCategories();

        saveButton.setOnAction(event -> saveProduct());
        cancelButton.setOnAction(event -> closeDialog());
    }

    private void loadCategories() {
        categoryComboBox.setItems(categoryService.getAllCategories());

        categoryComboBox.setConverter(new StringConverter<>() {
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

            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stockQuantity = Integer.parseInt(stockQuantityField.getText());
            String imageUrl = imageUrlField.getText();

            String specifications = getSpecifications();

            Product newProduct = new Product();
            newProduct.setName(name);
            newProduct.setCategoryId(selectedCategory.getId());
            newProduct.setCategoryName(selectedCategory.getName());
            newProduct.setDescription(description);
            newProduct.setPrice(price);
            newProduct.setStockQuantity(stockQuantity);
            newProduct.setImageUrl(imageUrl);
            newProduct.setSpecifications(specifications);
            newProduct.setCreatedAt(LocalDateTime.now());
            newProduct.setUpdatedAt(LocalDateTime.now());

            productService.saveProduct(newProduct);
            closeDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSpecifications() {
        Map<String, String> specs = new LinkedHashMap<>();
        specs.put("configMemory", configMemoryField.getText());
        specs.put("camera", cameraField.getText());
        specs.put("battery", batteryField.getText());
        specs.put("features", featuresField.getText());
        specs.put("connectivity", connectivityField.getText());
        specs.put("designMaterials", designMaterialsField.getText());

        Gson gson = new Gson();
        return gson.toJson(specs);
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
