package org.example.quanlybanhang.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.quanlybanhang.dao.CategoryDAO;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryController {
    @FXML
    public TextField searchField;
    @FXML
    private Button addCategoryButton;
    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, Integer> idColumn;
    @FXML
    private TableColumn<Category, String> nameColumn;
    @FXML
    private TableColumn<Category, String> descriptionColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<String> childCategoryCombo; // Đổi tên để phản ánh đúng chức năng

    private CategoryDAO categoryDAO;
    private ObservableList<Category> categoryList;

    public CategoryController() {
        Connection connection = DatabaseConnection.getConnection();
        categoryDAO = new CategoryDAO(connection);
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadCategories();

        // Thêm sự kiện khi click vào hàng trong bảng
        categoryTable.setOnMouseClicked(event -> {
            Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                nameField.setText(selectedCategory.getName());
                descriptionArea.setText(selectedCategory.getDescription());

                // Load danh mục con thay vì danh mục cha
                loadChildCategories(selectedCategory.getId());
            }
        });

        addCategoryButton.setOnAction(event -> DialogHelper.showDialog("/org/example/quanlybanhang/employeeManagementDialog.fxml", "Thêm Danh Mục Mới"));

    }

    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        categoryList = FXCollections.observableArrayList(categories);
        categoryTable.setItems(categoryList);
    }

    private void loadChildCategories(int parentId) {
        List<Category> childCategories = categoryDAO.getChildCategories(parentId);
        List<String> childCategoryNames = childCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        childCategoryCombo.setItems(FXCollections.observableArrayList(childCategoryNames));
        if (!childCategoryNames.isEmpty()) {
            childCategoryCombo.setValue(childCategoryNames.getFirst());
        }
    }

}
