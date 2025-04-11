package org.example.quanlybanhang.controller.category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.service.CategoryService;
import org.example.quanlybanhang.service.SearchService;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryController {

    @FXML
    private TextField searchField;

    @FXML
    private Button addCategoryButton;

    @FXML
    private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, Integer> idColumn;

    @FXML
    private TableColumn<Category, String> categoryColum;

    @FXML
    private TableColumn<Category, String> nameColumn;

    @FXML
    private TableColumn<Category, String> descriptionColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<String> childCategoryCombo;

    @FXML
    private Button saveButton;

    @FXML
    private RadioButton radioParent;

    @FXML
    private RadioButton radioChild;

    private ToggleGroup categoryTypeGroup;
    private ObservableList<Category> categoryList;
    private final CategoryService categoryService = new CategoryService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        categoryColum.setCellValueFactory(new PropertyValueFactory<>("category"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Setup radio group
        categoryTypeGroup = new ToggleGroup();
        radioParent.setToggleGroup(categoryTypeGroup);
        radioChild.setToggleGroup(categoryTypeGroup);
        radioParent.setSelected(true); // mặc định

        loadCategories();

        // Click vào hàng để fill data
        categoryTable.setOnMouseClicked(event -> {
            Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                nameField.setText(selectedCategory.getName());
                descriptionArea.setText(selectedCategory.getDescription());

                // Thiết lập lại radio
                if (selectedCategory.getParentId() == 0) {
                    radioParent.setSelected(true);
                } else {
                    radioChild.setSelected(true);
                }

                // Load danh mục con
                loadChildCategories(selectedCategory.getId());
            }
        });

        addCategoryButton.setOnAction(event ->
                DialogHelper.showDialog("/org/example/quanlybanhang/views/category/AddCategoryDialog.fxml", "Thêm Danh Mục Mới", (Stage) addCategoryButton.getScene().getWindow()
                )
        );

        saveButton.setOnAction(event -> handleSaveAction());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchCategories(newValue));

        radioChild.setOnAction(event -> {
            loadParentCategories();  // Gọi method mới để load danh mục cha vào combo box
        });
    }

    private void loadParentCategories() {
        List<Category> parentCategories = categoryList.stream()
                .filter(cat -> cat.getParentId() == 0)
                .toList();

        List<String> parentNames = parentCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        childCategoryCombo.setItems(FXCollections.observableArrayList(parentNames));

        if (!parentNames.isEmpty()) {
            childCategoryCombo.setValue(parentNames.getFirst());
        }
    }


    private void loadCategories() {
        categoryList = categoryService.getAllCategories();
        categoryTable.setItems(categoryList);
    }

    private void loadChildCategories(int parentId) {
        ObservableList<Category> childCategories = categoryService.getChildCategories(parentId);
        List<String> childCategoryNames = childCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        childCategoryCombo.setItems(FXCollections.observableArrayList(childCategoryNames));
        if (!childCategoryNames.isEmpty()) {
            childCategoryCombo.setValue(childCategoryNames.getFirst());
        }
    }

    @FXML
    private void handleSaveAction() {
        Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();

            selectedCategory.setName(name);
            selectedCategory.setDescription(description);

            // Xử lý danh mục cha hoặc con
            if (radioParent.isSelected()) {
                selectedCategory.setParentId(0);
            } else {
                String parentName = childCategoryCombo.getValue();
                if (parentName != null) {
                    Category parentCategory = categoryList.stream()
                            .filter(cat -> cat.getName().equals(parentName))
                            .findFirst()
                            .orElse(null);
                    if (parentCategory != null) {
                        selectedCategory.setParentId(parentCategory.getId());
                    }
                }
            }

            boolean success = categoryService.updateCategory(selectedCategory);
            if (success) {
                loadCategories();
                categoryTable.refresh();
                System.out.println("Cập nhật thành công!");
            } else {
                System.out.println("Cập nhật thất bại!");
            }
        }
    }

    private void searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadCategories();
        } else {
            List<Category> filteredCategories = SearchService.search(categoryList, keyword,
                    Category::getName, Category::getDescription);
            categoryTable.setItems(FXCollections.observableArrayList(filteredCategories));
        }
    }
}
