package org.example.quanlybanhang.controller.sale;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.quanlybanhang.controller.order.AddOrderDialogController;
import org.example.quanlybanhang.controller.sale.manager.*;
import org.example.quanlybanhang.model.Category;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.dto.productDTO.CartItem;
import org.example.quanlybanhang.service.*;
import org.example.quanlybanhang.utils.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SaleController {

    @FXML
    private Label totalLabel;
    @FXML
    private StackPane cartPane;
    @FXML
    private VBox cartBox;
    @FXML
    private GridPane gridPane;
    @FXML
    private Pagination pagination;
    @FXML
    private Button cartButton;
    @FXML
    private Slider minPriceSlider, maxPriceSlider;
    @FXML
    private Label minPriceLabel, maxPriceLabel;
    @FXML
    private VBox cartItemsContainer;
    @FXML
    private Button resetCartButton;
    @FXML
    private Button createOrderButton;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Category> categoryFilter;

    private static final int ITEMS_PER_PAGE = 12;

    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final IntegerProperty cartItemCount = new SimpleIntegerProperty(0);
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);

    private CartManager cartManager;
    private ProductDisplayManager displayManager;
    private PriceFilterManager priceFilterManager;
    private CartAnimationManager animationManager;
    private ProductService productService;
    private CartService cartService;
    private CategoryService categoryService;

    @FXML
    public void initialize() {
        setupServices();
        setupManagers();
        initComponents();
        loadCategoryData();
        loadProducts();
    }

    private void setupServices() {
        productService = new ProductService();
        cartService = new CartService();
        categoryService = new CategoryService();
    }

    private void setupManagers() {
        cartManager = new CartManager(cartItems, cartItemCount, cartItemsContainer, totalLabel, cartService);
        displayManager = new ProductDisplayManager(gridPane, productList, cartManager);
        priceFilterManager = new PriceFilterManager(
                minPriceSlider, maxPriceSlider,
                minPriceLabel, maxPriceLabel,
                filteredProducts, allProducts,
                this::resetPagination
        );
        animationManager = new CartAnimationManager(cartPane, cartBox);
    }

    private void initComponents() {
        animationManager.initCartSlideAnimation();
        priceFilterManager.initPriceSliders();
        cartItemCount.addListener((obs, oldVal, newVal) ->
                cartButton.setText("Giỏ hàng (" + newVal + ")"));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Add this for category filter
        categoryFilter.setOnAction(event -> {
            applyFilters();
        });
    }

    private void loadCategoryData() {
        try {
            categoryList.setAll(categoryService.getAllCategories());
            categoryFilter.setItems(categoryList);
            // Add a "All Categories" option at the beginning
            Category allCategories = new Category();
            allCategories.setName("Tất cả");
            allCategories.setId(0);
            categoryFilter.getItems().add(0, allCategories);
            categoryFilter.setValue(allCategories); // Set default value
        } catch (Exception e) {
            System.out.println("Không thể tải danh mục: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        try {
            allProducts.setAll(productService.getAllProducts());
            filteredProducts.setAll(allProducts);
            setupPagination();
        } catch (Exception e) {
            System.out.println("Không thể tải sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupPagination() {
        PaginationUtils.setup(
                pagination,
                filteredProducts,
                productList,
                currentPage,
                ITEMS_PER_PAGE,
                (pagedData, pageIndex) -> displayManager.displayProducts()
        );
    }

    private void resetPagination() {
        currentPage.set(0);
        pagination.setCurrentPageIndex(0);
        pagination.setPageCount(Math.max(1,
                (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE)));
        updatePagedItems();
    }

    private void updatePagedItems() {
        int startIndex = currentPage.get() * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredProducts.size());
        productList.setAll(filteredProducts.subList(startIndex, endIndex));
        displayManager.displayProducts();
    }

    @FXML
    private void toggleCart() {
        animationManager.toggleCart();
    }

    @FXML
    private void closeCart() {
        animationManager.closeCart();
    }

    @FXML
    private void resetCart() {
        cartManager.clearCart();
    }

    @FXML
    private void handleCreateOrder() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlybanhang/views/order/AddOrderDialog.fxml"));
            Parent root = loader.load();
            AddOrderDialogController orderController = loader.getController();
            orderController.setCartItems(cartItems);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Thêm Đơn Hàng");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
    }

    private void applyFilters() {
        String keyword = searchField.getText();
        Category selectedCategory = categoryFilter.getValue();
        List<Product> filtered = allProducts;
        if (selectedCategory != null && selectedCategory.getId() != 0) {
            filtered = filtered.stream()
                    .filter(p -> p.getCategoryId() == selectedCategory.getId())
                    .collect(Collectors.toList());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            filtered = SearchService.search(
                    filtered,
                    keyword,
                    Product::getName,
                    Product::getDescription,
                    e -> String.valueOf(e.getPrice())
            );
        }
        filteredProducts.setAll(filtered);
        resetPagination();
    }
}