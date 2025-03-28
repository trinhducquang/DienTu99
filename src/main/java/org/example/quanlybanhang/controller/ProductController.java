package org.example.quanlybanhang.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.quanlybanhang.dao.CategoryDAO;
import org.example.quanlybanhang.dao.ProductDAO;
import org.example.quanlybanhang.database.DatabaseConnection;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.service.SearchService;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

public class ProductController {
    @FXML
    private Button addProductButton;
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, Integer> categoryIdColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockQuantityColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private ComboBox<Category> categoryFilter;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<Product, Void> actionColumn;

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadProductData();
        loadCategoryData();
        addActionButtons();
        productsTable.setItems(productList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterBySearch(newValue));

        addProductButton.setOnAction(event -> DialogHelper.showDialog("/org/example/quanlybanhang/ProductDialog.fxml", "Thêm Sản Phẩm Mới"));
        categoryFilter.setOnAction(event -> filterProducts());
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            private final HBox buttonGroup = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    System.out.println("Sửa sản phẩm: " + product.getName());
                });

                deleteButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    System.out.println("Xóa sản phẩm: " + product.getName());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonGroup);
            }
        });
    }

    private void filterBySearch(String keyword) {
        List<Product> filteredProducts = SearchService.searchProducts(allProducts, keyword);
        productList.setAll(filteredProducts);
    }

    private void loadProductData() {
        Connection connection = DatabaseConnection.getConnection();
        ProductDAO productDAO = new ProductDAO(connection);
        List<Product> products = productDAO.getAllProducts();
        allProducts.addAll(products);
        productList.addAll(products);
    }

    private void loadCategoryData() {
        Connection connection = DatabaseConnection.getConnection();
        CategoryDAO categoryDAO = new CategoryDAO(connection);
        List<Category> categories = categoryDAO.getAllCategories();
        categoryFilter.setItems(FXCollections.observableArrayList(categories));
    }

    private void filterProducts() {
        Category selectedCategory = categoryFilter.getValue();
        if (selectedCategory == null) {
            productList.setAll(allProducts);
        } else {
            List<Product> filteredProducts = allProducts.stream()
                    .filter(product -> product.getCategoryId() == selectedCategory.getId())
                    .collect(Collectors.toList());
            productList.setAll(filteredProducts);
        }
    }
}