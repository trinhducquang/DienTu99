package org.example.dientu99.controller.product;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.example.dientu99.controller.interfaces.RefreshableView;
import org.example.dientu99.enums.ProductStatus;
import org.example.dientu99.helpers.DialogHelper;
import org.example.dientu99.model.Category;
import org.example.dientu99.model.Product;
import org.example.dientu99.service.CategoryService;
import org.example.dientu99.service.ProductService;
import org.example.dientu99.utils.PaginationUtils;
import org.example.dientu99.utils.TableCellFactoryUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public class ProductController implements RefreshableView {

    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryNameColumn;
    @FXML
    private TableColumn<Product, BigDecimal> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockQuantityColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, ProductStatus> statusColumn;
    @FXML
    private TableColumn<Product, String> imageColumn;
    @FXML
    private TableColumn<Product, Void> OperationColumn;
    @FXML
    private Pagination pagination;
    @FXML
    private ComboBox<Category> categoryFilter;
    @FXML
    private TextField searchField;
    @FXML
    private Button addProductButton;

    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> currentPageItems = FXCollections.observableArrayList();
    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final int itemsPerPage = 18;

    @FXML
    public void initialize() {
        System.out.println("🔄 Đang khởi tạo ProductController");

        setupTable();
        loadInitialData();
        setupBindings();
        setupPagination();
        setupAddProductButton();
        setupSearchAndFilter();

        System.out.println("✅ Đã khởi tạo xong ProductController");
    }

    private void setupTable() {
        productsTable.setEditable(true);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        priceColumn.setCellFactory(TableCellFactoryUtils.currencyCellFactory());

        setupEditableColumns();
        setupOperationColumn();
    }

    private void setupEditableColumns() {
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setName(event.getNewValue());
            updateProduct(product);
        });

        priceColumn.setCellFactory(TableCellFactoryUtils.editableCurrencyCellFactory());
        priceColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setPrice(event.getNewValue());
            updateProduct(product);
            productsTable.refresh();
        });

        categoryNameColumn.setCellFactory(column -> {
            ComboBoxTableCell<Product, String> cell = new ComboBoxTableCell<>() {
                @Override
                public void startEdit() {
                    if (getTableColumn().isEditable() && getTableView().isEditable() && getTableRow().getItem() != null) {
                        getItems().setAll(categoryList.stream().map(Category::getName).toList());
                        super.startEdit();
                    }
                }
            };
            cell.getItems().setAll(categoryList.stream().map(Category::getName).toList());
            return cell;
        });

        categoryNameColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            String oldCategoryName = product.getCategoryName();
            String newCategoryName = event.getNewValue();

            if (!oldCategoryName.equals(newCategoryName)) {
                Category selectedCategory = categoryList.stream()
                        .filter(c -> c.getName().equals(newCategoryName))
                        .findFirst()
                        .orElse(null);

                if (selectedCategory != null) {
                    product.setCategoryName(newCategoryName);
                    product.setCategoryId(selectedCategory.getId());
                    updateProduct(product);
                }
            }
        });

        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setDescription(event.getNewValue());
            updateProduct(product);
        });

        imageColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        imageColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setImageUrl(event.getNewValue());
            updateProduct(product);
        });

        ObservableList<ProductStatus> statusOptions = FXCollections.observableArrayList(ProductStatus.values());
        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(statusOptions));
        statusColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setStatus(event.getNewValue());
            updateProduct(product);
        });
    }

    private void setupOperationColumn() {
        OperationColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailButton = new Button("Chi tiết sản phẩm");

            {
                detailButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    if (product != null) {
                        DialogHelper.showProductDialog(
                                "/org/example/dientu99/views/product/Product_detailsDialog.fxml",
                                "Chi tiết sản phẩm",
                                product.getId(),
                                (Stage) detailButton.getScene().getWindow()
                        );
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

    private void loadInitialData() {
        allProducts.setAll(productService.getAllProducts());
        categoryList.setAll(categoryService.getAllCategories());
        categoryFilter.setItems(categoryList);
        filteredProducts.setAll(allProducts);
    }

    private void setupSearchAndFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        categoryFilter.setOnAction(event -> applyFilter());
    }

    private void setupAddProductButton() {
        addProductButton.setOnAction(event ->
                DialogHelper.showDialog(
                        "/org/example/dientu99/views/product/ProductDialog.fxml",
                        "Thêm Sản Phẩm",
                        (Stage) addProductButton.getScene().getWindow(),
                        this
                )
        );
    }

    // === Thiết lập phân trang ===
    private void setupPagination() {
        PaginationUtils.setup(
                pagination,
                filteredProducts,
                currentPageItems,
                currentPage,
                itemsPerPage,
                (page, pageIndex) -> {
                    productsTable.setItems(currentPageItems);
                    productsTable.refresh();
                    System.out.println("🧾 Trang " + (pageIndex + 1) + ": " + page.size() + " sản phẩm");
                }
        );
    }

    private void updateProduct(Product product) {
        productService.updateProduct(product);
        int index = allProducts.indexOf(product);
        if (index >= 0) {
            allProducts.set(index, product);
        }
        applyFilter();
    }


    private void applyFilter() {
        String keyword = searchField.getText();
        Category selected = categoryFilter.getValue();

        List<Product> result = allProducts.stream()
                .filter(p -> selected == null || selected.getName().equals(p.getCategoryName()))
                .filter(p -> keyword == null || keyword.isEmpty() || containsKeyword(p, keyword))
                .toList();

        filteredProducts.setAll(result);
        setupPagination();
    }

    private boolean containsKeyword(Product product, String keyword) {
        keyword = keyword.toLowerCase();
        String finalKeyword = keyword;
        return Stream.of(
                String.valueOf(product.getId()),
                product.getName(),
                product.getCategoryName(),
                product.getDescription(),
                String.valueOf(product.getPrice()),
                String.valueOf(product.getStockQuantity())
        ).anyMatch(field -> field != null && field.toLowerCase().contains(finalKeyword));
    }

    @Override
    public void refresh() {
        System.out.println("🔁 Refresh Product View");

        loadInitialData();
        applyFilter();

        System.out.println("✅ Refresh hoàn tất");
    }


    private void setupBindings() {

    }
}
