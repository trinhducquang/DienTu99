package org.example.quanlybanhang.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.example.quanlybanhang.dao.CategoryDAO;
import org.example.quanlybanhang.dao.ProductDAO;
import org.example.quanlybanhang.utils.DatabaseConnection;
import org.example.quanlybanhang.enums.ProductStatus;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.service.SearchService;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

public class ProductController {
    @FXML
    private TableColumn<Product, Void> OperationColumn;
    @FXML
    private Button addProductButton;
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryNameColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockQuantityColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, ProductStatus> statusColumn;
    @FXML
    private TableColumn<Product, String> imageColumn;
    @FXML
    private ComboBox<Category> categoryFilter;
    @FXML
    private TextField searchField;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        productsTable.setEditable(true);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        setEditableColumns();
        loadProductData();
        loadCategoryData();
        productsTable.setItems(productList);
        addButtonToActionColumn();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterBySearch(newValue));
        addProductButton.setOnAction(event -> DialogHelper.showDialog("/org/example/quanlybanhang/ProductDialog.fxml", "Thêm Sản Phẩm Mới"));
        categoryFilter.setOnAction(event -> filterProducts());
    }

    private void addButtonToActionColumn() {
        OperationColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailButton = new Button("Chi tiết sản phẩm");

            {
                detailButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    if (product != null) {
                        DialogHelper.showDialog("/org/example/quanlybanhang/Product_detailsDialog.fxml", "Chi tiết sản phẩm", product.getId());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : detailButton);
            }
        });
    }


    private void setEditableColumns() {
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setName(event.getNewValue());
            updateProductInDatabase(product);
        });

        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setPrice(event.getNewValue());
            updateProductInDatabase(product);
        });

        stockQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        stockQuantityColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setStockQuantity(event.getNewValue());
            updateProductInDatabase(product);
        });

        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setDescription(event.getNewValue());
            updateProductInDatabase(product);
        });

        imageColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        imageColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setImageUrl(event.getNewValue());
            updateProductInDatabase(product);
        });

        ObservableList<ProductStatus> statusOptions = FXCollections.observableArrayList(ProductStatus.values());
        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(statusOptions));
        statusColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setStatus(event.getNewValue());
            updateProductInDatabase(product);
        });
    }

    private void updateProductInDatabase(Product product) {
        Connection connection = DatabaseConnection.getConnection();
        ProductDAO productDAO = new ProductDAO(connection);
        productDAO.updateProduct(product);
        loadProductData();
    }

    private void loadProductData() {
        Connection connection = DatabaseConnection.getConnection();
        ProductDAO productDAO = new ProductDAO(connection);
        List<Product> products = productDAO.getAllProducts();
        allProducts.setAll(products);
        productList.setAll(products);
    }

    private void loadCategoryData() {
        Connection connection = DatabaseConnection.getConnection();
        CategoryDAO categoryDAO = new CategoryDAO(connection);
        List<Category> categories = categoryDAO.getAllCategories();
        categoryFilter.setItems(FXCollections.observableArrayList(categories));
    }

    private void filterBySearch(String keyword) {
        List<Product> filteredProducts = SearchService.search(allProducts, keyword, Product::getName);
        productList.setAll(filteredProducts);
    }

    private void filterProducts() {
        Category selectedCategory = categoryFilter.getValue();
        if (selectedCategory == null) {
            productList.setAll(allProducts);
        } else {
            String selectedCategoryName = selectedCategory.getName();
            List<Product> filteredProducts = allProducts.stream()
                    .filter(product -> selectedCategoryName.equals(product.getCategoryName()))
                    .collect(Collectors.toList());
            productList.setAll(filteredProducts);
        }
    }
}
