package org.example.quanlybanhang.controller.category;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.service.CategoryService;
import org.example.quanlybanhang.utils.AlertUtils;

public class AddCategoryDialogController {

    @FXML
    private TextField categoryNameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<Category> parentCategoryComboBox;

    @FXML
    private RadioButton rootCategoryRadio;

    private final CategoryService categoryService = new CategoryService();

    @FXML
    public void initialize() {
        if (parentCategoryComboBox != null) {
            loadParentCategories();
        }

        if (rootCategoryRadio != null && parentCategoryComboBox != null) {
            rootCategoryRadio.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                parentCategoryComboBox.disableProperty().unbind();
                parentCategoryComboBox.setDisable(isNowSelected);
            });
        }
    }

    private void loadParentCategories() {
        parentCategoryComboBox.setItems(categoryService.getAllCategories());
    }

    @FXML
    private void saveButton() {
        String name = categoryNameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            AlertUtils.showWarning("Lỗi", "Tên danh mục không được để trống.");
            categoryNameField.requestFocus();
            return;
        }

        int parentId = (rootCategoryRadio.isSelected() || parentCategoryComboBox.getValue() == null)
                ? 0
                : parentCategoryComboBox.getValue().getId();

        Category newCategory = new Category(0, name, description, parentId, null);
        boolean success = categoryService.addCategory(newCategory);

        if (success) {
            AlertUtils.showInfo("Thành công", "Thêm danh mục thành công.");
            closeDialog();
        } else {
            AlertUtils.showError("Lỗi", "Không thể thêm danh mục.");
        }
    }

    @FXML
    private void cancelButton() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) categoryNameField.getScene().getWindow();
        stage.close();
    }
}