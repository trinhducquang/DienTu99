package org.example.quanlybanhang.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.quanlybanhang.dao.CategoryDAO;
import org.example.quanlybanhang.dao.ProductDAO;
import org.example.quanlybanhang.database.DatabaseConnection;
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

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up the columns in the table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Load data from the database
        loadProductData();
        loadCategoryData();

        // Set the data to the table
        productsTable.setItems(productList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterBySearch(newValue));

        addProductButton.setOnAction(event -> showProductDialog());
        categoryFilter.setOnAction(event -> filterProducts());
    }

    private void filterBySearch(String keyword) {
        List<Product> filteredProducts = SearchService.searchProducts(allProducts, keyword);
        productList.setAll(filteredProducts);
    }

    private void showProductDialog() {
        try {
            // Load ProductDialog.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlybanhang/ProductDialog.fxml"));
            Parent root = loader.load();

            // Create a new stage (popup)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Thêm Sản Phẩm Mới");
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
            dialogStage.initStyle(StageStyle.UTILITY);

            // Set the scene for the dialog
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            // Show the dialog
            dialogStage.showAndWait(); // Show and wait for it to be closed
        } catch (Exception e) {
            e.printStackTrace();
        }
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