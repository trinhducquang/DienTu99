package org.example.quanlybanhang.controller.product;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.example.quanlybanhang.enums.*;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.*;
import org.example.quanlybanhang.service.CategoryService;
import org.example.quanlybanhang.service.ProductService;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProductController {
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryNameColumn;
    @FXML private TableColumn<Product, BigDecimal> priceColumn;
    @FXML private TableColumn<Product, Integer> stockQuantityColumn;
    @FXML private TableColumn<Product, String> descriptionColumn;
    @FXML private TableColumn<Product, ProductStatus> statusColumn;
    @FXML private TableColumn<Product, String> imageColumn;
    @FXML private TableColumn<Product, Void> OperationColumn;
    @FXML private Pagination pagination;

    @FXML private ComboBox<Category> categoryFilter;
    @FXML private TextField searchField;
    @FXML private Button addProductButton;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final int itemsPerPage = 18;



    @FXML
    public void initialize() {
        productsTable.setEditable(true);
        setupTableColumns();
        loadCategoryData();
        setupSearchAndFilter();
        setupAddProductButton();
        addButtonToActionColumn();

        loadAllProducts(); // Load tất cả sản phẩm vào allProducts
        setupPagination();
    }


    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        priceColumn.setCellFactory(TableCellFactoryUtils.currencyCellFactory());

    }

    private void setEditableColumns() {
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setName(event.getNewValue());
            updateProductInDatabase(product);
        });

        priceColumn.setCellFactory(TableCellFactoryUtils.editableCurrencyCellFactory());
        priceColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setPrice(event.getNewValue());
            updateProductInDatabase(product);
            productsTable.refresh();
        });

        categoryNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(
                        categoryList.stream().map(Category::getName).toList()
                )
        ));
        categoryNameColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            String newCategoryName = event.getNewValue();
            product.setCategoryName(newCategoryName);

            categoryList.stream()
                    .filter(c -> c.getName().equals(newCategoryName))
                    .findFirst()
                    .ifPresent(c -> product.setCategoryId(c.getId()));

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

    private void addButtonToActionColumn() {
        OperationColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailButton = new Button("Chi tiết sản phẩm");

            {
                detailButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    if (product != null) {
                        DialogHelper.showProductDialog(
                                "/org/example/quanlybanhang/views/product/Product_detailsDialog.fxml",
                                "Chi tiết sản phẩm",
                                product.getId(), (Stage) detailButton.getScene().getWindow()
                        );
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size() || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null);
                } else {
                    setGraphic(detailButton);
                }
            }
        });
    }


    private void setupSearchAndFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterBySearch(newVal));
        categoryFilter.setOnAction(event -> filterProducts());
    }

    private void setupAddProductButton() {
        addProductButton.setOnAction(event ->
                DialogHelper.showDialog("/org/example/quanlybanhang/views/product/ProductDialog.fxml", "Thêm Sản Phẩm Mới" , (Stage) addProductButton.getScene().getWindow())
        );
    }

    private void updateProductInDatabase(Product product) {
        productService.updateProduct(product);
        loadProductData();
    }

    private void loadProductData() {
        List<Product> products = productService.getAllProducts();
        allProducts.setAll(products);
        productList.setAll(products);
    }


    private void loadCategoryData() {
        categoryList.setAll(categoryService.getAllCategories());
        categoryFilter.setItems(categoryList);

        setEditableColumns(); // Vẫn giữ lại dòng này để gọi sau khi load xong danh mục
    }


    private void filterBySearch(String keyword) {
        List<Product> filtered = SearchService.search(allProducts, keyword,
                e -> String.valueOf(e.getId()),
                e -> String.valueOf(e.getPrice()),
                e -> String.valueOf(e.getStockQuantity()),
                Product::getName,
                Product::getCategoryName,
                Product::getDescription
        );
        productList.setAll(filtered);
    }

    private void filterProducts() {
        Category selected = categoryFilter.getValue();
        if (selected == null) {
            productList.setAll(allProducts);
        } else {
            String name = selected.getName();
            List<Product> filtered = allProducts.stream()
                    .filter(p -> name.equals(p.getCategoryName()))
                    .collect(Collectors.toList());
            productList.setAll(filtered);
        }
    }

    private void loadAllProducts() {
        List<Product> products = productService.getAllProducts();
        allProducts.setAll(products);
    }

    private void setupPagination() {
        PaginationUtils.setup(
                pagination,
                allProducts,
                productList,
                currentPage,
                itemsPerPage,
                (pagedData, pageIndex) -> productsTable.setItems(productList)
        );
    }

}
