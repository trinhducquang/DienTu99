package org.example.dientu99.controller.sale;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dientu99.controller.ui.animation.CartAnimationUI;
import org.example.dientu99.controller.sale.manager.CartManager;
import org.example.dientu99.controller.sale.manager.PriceFilterManager;
import org.example.dientu99.controller.sale.manager.ProductDisplayManager;
import org.example.dientu99.dto.productDTO.CartItem;
import org.example.dientu99.helpers.DialogHelper;
import org.example.dientu99.helpers.LogoutHandler;
import org.example.dientu99.model.Category;
import org.example.dientu99.model.Product;
import org.example.dientu99.service.CartService;
import org.example.dientu99.service.CategoryService;
import org.example.dientu99.service.ProductService;
import org.example.dientu99.service.SearchService;
import org.example.dientu99.utils.PaginationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SaleController {

    @FXML
    private Button logoutButton;
    @FXML
    private Button orderButton;
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
    private CartAnimationUI animationManager;
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
        orderButton.setOnAction(event -> handleOrderButton());
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
        animationManager = new CartAnimationUI(cartPane, cartBox);
    }

    private void initComponents() {
        animationManager.initCartSlideAnimation();
        priceFilterManager.initPriceSliders();
        cartItemCount.addListener((obs, oldVal, newVal) ->
                cartButton.setText("Giỏ hàng (" + newVal + ")"));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        categoryFilter.setOnAction(event -> {
            applyFilters();
        });
    }

    private void loadCategoryData() {
        try {
            categoryList.setAll(categoryService.getAllCategories());
            categoryFilter.setItems(categoryList);
            Category allCategories = new Category();
            allCategories.setName("Tất cả");
            allCategories.setId(0);
            categoryFilter.getItems().addFirst(allCategories);
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

    @FXML
    private void handleOrderButton() {
        try {
            DialogHelper.showDialog(
                    "/org/example/dientu99/views/order/Order.fxml",
                    "Quản Lý Đơn Hàng",
                    (Stage) orderButton.getScene().getWindow()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateOrder() {
        try {
            Stage currentStage = (Stage) createOrderButton.getScene().getWindow();
            DialogHelper.showOrderCreationDialog(
                    "/org/example/dientu99/views/order/AddOrderDialog.fxml",
                    "Thêm Đơn Hàng",
                    cartItems,
                    currentStage
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        LogoutHandler.handleLogout(logoutButton);
    }

}