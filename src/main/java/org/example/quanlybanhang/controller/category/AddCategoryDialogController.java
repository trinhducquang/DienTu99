package org.example.quanlybanhang.controller.category;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.CategoryDAO;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.List;

public class AddCategoryDialogController {

    @FXML
    private TextField categoryNameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<Category> parentCategoryComboBox;

    @FXML
    private RadioButton rootCategoryRadio;

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
        Connection connection = DatabaseConnection.getConnection();
        CategoryDAO categoryDAO = new CategoryDAO(connection);
        List<Category> categories = categoryDAO.getAllCategories();

        if (parentCategoryComboBox != null) {
            parentCategoryComboBox.getItems().setAll(categories);
        }
    }

    @FXML
    private void saveButton() {
        String name = categoryNameField.getText().trim();
        String description = descriptionField.getText().trim();

        int parentId = (rootCategoryRadio.isSelected() || parentCategoryComboBox.getValue() == null)
                ? 0
                : parentCategoryComboBox.getValue().getId();

        if (name.isEmpty()) {
            showAlert("Error", "Category name cannot be empty.");
            return;
        }

        Category newCategory = new Category(0, name, description, parentId, null);

        Connection connection = DatabaseConnection.getConnection();
        CategoryDAO categoryDAO = new CategoryDAO(connection);
        boolean success = categoryDAO.createCategory(newCategory);

        if (success) {
            showAlert("Success", "Category thêm thành công.");
            closeDialog();
        } else {
            showAlert("Error", "Unable to add category.");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
