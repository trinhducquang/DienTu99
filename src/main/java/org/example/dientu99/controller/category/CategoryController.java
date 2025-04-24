package org.example.dientu99.controller.category;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.dientu99.controller.interfaces.RefreshableView;
import org.example.dientu99.helpers.DialogHelper;
import org.example.dientu99.model.Category;
import org.example.dientu99.service.CategoryService;
import org.example.dientu99.service.SearchService;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryController implements RefreshableView {

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
    private ObservableList<Category> fullCategoryList = FXCollections.observableArrayList();
    private final ObservableList<Category> pagedCategoryList = FXCollections.observableArrayList();
    private final CategoryService categoryService = new CategoryService();

    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final int itemsPerPage = 18;

    @Override
    public void refresh() {
        loadCategories();
        setupPagination();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupToggleGroup();
        setupListeners();
        refresh();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        categoryColum.setCellValueFactory(new PropertyValueFactory<>("category"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void setupToggleGroup() {
        categoryTypeGroup = new ToggleGroup();
        radioParent.setToggleGroup(categoryTypeGroup);
        radioChild.setToggleGroup(categoryTypeGroup);
        radioParent.setSelected(true);
    }

    private void setupListeners() {
        addCategoryButton.setOnAction(event ->
                DialogHelper.showDialog(
                        "/org/example/dientu99/views/category/AddCategoryDialog.fxml",
                        "Thêm Danh Mục Mới",
                        (Stage) addCategoryButton.getScene().getWindow(),
                        this
                )
        );

        saveButton.setOnAction(event -> handleSaveAction());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchCategories(newVal));

        radioChild.setOnAction(event -> loadParentCategories());

        categoryTable.setOnMouseClicked(event -> populateFieldsFromSelected());
    }

    private void loadCategories() {
        fullCategoryList.setAll(categoryService.getAllCategories());
        updatePageData();
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) fullCategoryList.size() / itemsPerPage);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(currentPage.get());

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            currentPage.set(newIndex.intValue());
            updatePageData();
        });
    }

    private void updatePageData() {
        int start = currentPage.get() * itemsPerPage;
        int end = Math.min(start + itemsPerPage, fullCategoryList.size());
        pagedCategoryList.setAll(fullCategoryList.subList(start, end));
        categoryTable.setItems(pagedCategoryList);
    }

    private void searchCategories(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            updatePageData();
            return;
        }

        List<Category> filtered = SearchService.search(fullCategoryList, keyword,
                Category::getName, Category::getDescription);
        pagedCategoryList.setAll(filtered);
        categoryTable.setItems(pagedCategoryList);

        // Optional: update pagination
        pagination.setPageCount(1);
        pagination.setCurrentPageIndex(0);
    }

    private void loadParentCategories() {
        List<String> parentNames = fullCategoryList.stream()
                .filter(cat -> cat.getParentId() == 0)
                .map(Category::getName)
                .collect(Collectors.toList());

        childCategoryCombo.setItems(FXCollections.observableArrayList(parentNames));
        if (!parentNames.isEmpty()) {
            childCategoryCombo.setValue(parentNames.get(0));
        }
    }

    private void loadChildCategories(int parentId) {
        List<String> childNames = categoryService.getChildCategories(parentId).stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        childCategoryCombo.setItems(FXCollections.observableArrayList(childNames));
        if (!childNames.isEmpty()) {
            childCategoryCombo.setValue(childNames.get(0));
        }
    }

    private void populateFieldsFromSelected() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        nameField.setText(selected.getName());
        descriptionArea.setText(selected.getDescription());

        if (selected.getParentId() == 0) {
            radioParent.setSelected(true);
        } else {
            radioChild.setSelected(true);
            loadChildCategories(selected.getId());
        }
    }

    private void handleSaveAction() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        selected.setName(nameField.getText().trim());
        selected.setDescription(descriptionArea.getText().trim());

        if (radioParent.isSelected()) {
            selected.setParentId(0);
        } else {
            String parentName = childCategoryCombo.getValue();
            Category parent = fullCategoryList.stream()
                    .filter(cat -> cat.getName().equals(parentName))
                    .findFirst()
                    .orElse(null);
            if (parent != null) {
                selected.setParentId(parent.getId());
            }
        }

        boolean updated = categoryService.updateCategory(selected);
        if (updated) {
            loadCategories();
            categoryTable.refresh();
        } else {
            System.err.println("Cập nhật thất bại!");
        }
    }
}
