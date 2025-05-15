package org.example.dientu99.controller.product;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.dientu99.model.*;
import org.example.dientu99.service.*;
import org.example.dientu99.utils.AlertUtils;
import org.example.dientu99.utils.TextFieldFormatterUtils;
import org.example.dientu99.controller.interfaces.RefreshableView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class ProductDialogController {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    private RefreshableView parentController;

    @FXML
    private TextField productNameField, priceField, descriptionField, imageUrlField;
    @FXML private TextField configMemoryField, cameraField, batteryField, featuresField, connectivityField, designMaterialsField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private Button saveButton, cancelButton;

    @FXML
    public void initialize() {
        TextFieldFormatterUtils.applyBlazingFastCurrencyFormat(priceField);
        loadCategories();
        saveButton.setOnAction(event -> saveProduct());
        cancelButton.setOnAction(event -> closeDialog());

        System.out.println("ProductDialogController đã khởi tạo");
    }

    public void setParentController(RefreshableView parentController) {
        System.out.println("setParentController được gọi với controller: " +
                (parentController != null ? parentController.getClass().getName() : "null"));
        this.parentController = parentController;
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
            if (!isValidInput()) {
                return;
            }

            String name = productNameField.getText();
            Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
            String description = descriptionField.getText();
            BigDecimal price = TextFieldFormatterUtils.parseCurrencyText(priceField.getText());
            String imageUrl = imageUrlField.getText();

            String specifications = getSpecifications();

            Product newProduct = new Product();
            newProduct.setName(name);
            newProduct.setCategoryId(selectedCategory.getId());
            newProduct.setCategoryName(selectedCategory.getName());
            newProduct.setDescription(description);
            newProduct.setPrice(price);
            newProduct.setImageUrl(imageUrl);
            newProduct.setSpecifications(specifications);
            newProduct.setCreatedAt(LocalDateTime.now());
            newProduct.setUpdatedAt(LocalDateTime.now());

            productService.saveProduct(newProduct);
            System.out.println("Đã lưu sản phẩm thành công");

            closeDialog();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Đã xảy ra lỗi khi lưu sản phẩm: " + e.getMessage());
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
        System.out.println("Đóng dialog");
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private boolean isValidInput() {
        if (productNameField.getText().isBlank()
                || priceField.getText().isBlank()
                || descriptionField.getText().isBlank()
                || imageUrlField.getText().isBlank()
                || categoryComboBox.getSelectionModel().getSelectedItem() == null) {

            AlertUtils.showWarning("Thiếu thông tin", "Vui lòng điền đầy đủ các trường bắt buộc và chọn danh mục.");
            return false;
        }

        try {
            TextFieldFormatterUtils.parseCurrencyText(priceField.getText());
        } catch (NumberFormatException e) {
            AlertUtils.showWarning("Sai định dạng", "Giá không hợp lệ.");
            return false;
        }

        return true;
    }
}