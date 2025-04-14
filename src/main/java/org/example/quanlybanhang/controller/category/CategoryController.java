package org.example.quanlybanhang.controller.category;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import org.example.quanlybanhang.utils.PaginationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryController {

    @FXML private TextField searchField;
    @FXML private Button addCategoryButton;
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> idColumn;
    @FXML private TableColumn<Category, String> categoryColum;
    @FXML private TableColumn<Category, String> nameColumn;
    @FXML private TableColumn<Category, String> descriptionColumn;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> childCategoryCombo;
    @FXML private Button saveButton;
    @FXML private RadioButton radioParent;
    @FXML private RadioButton radioChild;
    @FXML private Pagination pagination;

    private ToggleGroup categoryTypeGroup;
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private final ObservableList<Category> pagedCategoryList = FXCollections.observableArrayList();
    private final CategoryService categoryService = new CategoryService();

    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final int itemsPerPage = 5;  // Số mục trên mỗi trang

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
        setupPagination();

        addCategoryButton.setOnAction(event ->
                DialogHelper.showDialog("/org/example/quanlybanhang/views/category/AddCategoryDialog.fxml", "Thêm Danh Mục Mới", (Stage) addCategoryButton.getScene().getWindow())
        );

        saveButton.setOnAction(event -> handleSaveAction());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchCategories(newValue));

        radioChild.setOnAction(event -> loadParentCategories());

        categoryTable.setOnMouseClicked(event -> {
            Category selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                nameField.setText(selectedCategory.getName());
                descriptionArea.setText(selectedCategory.getDescription());

                if (selectedCategory.getParentId() == 0) {
                    radioParent.setSelected(true);
                } else {
                    radioChild.setSelected(true);
                }

                loadChildCategories(selectedCategory.getId());
            }
        });
    }

    private void loadCategories() {
        categoryList = FXCollections.observableArrayList(categoryService.getAllCategories());
        filterAndPaginateCategories();
    }

    private void filterAndPaginateCategories() {
        List<Category> filteredCategories = categoryList.stream()
                .collect(Collectors.toList());  // Add filtering logic if needed
        pagedCategoryList.setAll(filteredCategories);
    }

    private void setupPagination() {
        pagination.setPageCount((int) Math.ceil((double) categoryList.size() / itemsPerPage));
        pagination.setCurrentPageIndex(currentPage.get());

        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            currentPage.set(newValue.intValue());
            updatePageData();
        });
        updatePageData();
    }

    private void updatePageData() {
        int start = currentPage.get() * itemsPerPage;
        int end = Math.min(start + itemsPerPage, categoryList.size());
        pagedCategoryList.setAll(categoryList.subList(start, end));
        categoryTable.setItems(pagedCategoryList);
    }

    private void searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadCategories();
        } else {
            List<Category> filteredCategories = SearchService.search(categoryList, keyword,
                    Category::getName, Category::getDescription);
            pagedCategoryList.setAll(filteredCategories);
            categoryTable.setItems(pagedCategoryList);
        }
    }

    private void loadParentCategories() {
        List<Category> parentCategories = categoryList.stream()
                .filter(cat -> cat.getParentId() == 0)
                .collect(Collectors.toList());

        List<String> parentNames = parentCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        childCategoryCombo.setItems(FXCollections.observableArrayList(parentNames));

        if (!parentNames.isEmpty()) {
            childCategoryCombo.setValue(parentNames.get(0));
        }
    }

    private void loadChildCategories(int parentId) {
        ObservableList<Category> childCategories = FXCollections.observableArrayList(
                categoryService.getChildCategories(parentId)
        );
        List<String> childCategoryNames = childCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        childCategoryCombo.setItems(FXCollections.observableArrayList(childCategoryNames));
        if (!childCategoryNames.isEmpty()) {
            childCategoryCombo.setValue(childCategoryNames.get(0));
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
}
