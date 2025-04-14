// Refactored SaleController
package org.example.quanlybanhang.controller.sale;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.quanlybanhang.controller.sale.manager.*;
import org.example.quanlybanhang.service.ProductService;

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

    private CartManager cartManager;
    private ProductDisplayManager productManager;
    private PriceFilterManager priceFilterManager;
    private CartAnimationManager animationManager;

    private final ProductService productService;

    // Dependency Injection qua constructor
    public SaleController() {
        this.productService = new ProductService();
    }

    @FXML
    public void initialize() {
        // Khởi tạo các manager
        cartManager = new CartManager(cartItemsContainer, totalLabel);
        animationManager = new CartAnimationManager(cartPane, cartBox);
        priceFilterManager = new PriceFilterManager(
                minPriceSlider, maxPriceSlider,
                minPriceLabel, maxPriceLabel
        );
        productManager = new ProductDisplayManager(gridPane, pagination, cartManager);

        // Thiết lập liên kết giữa các manager
        setupManagerConnections();

        // Load dữ liệu ban đầu
        loadData();
    }

    private void setupManagerConnections() {
        // Kết nối các sự kiện giữa các manager
        cartManager.cartItemCountProperty().addListener(
                (obs, oldVal, newVal) -> cartButton.setText("Giỏ hàng (" + newVal + ")")
        );

        priceFilterManager.setOnPriceFilterChanged(
                (min, max) -> productManager.filterByPrice(min, max)
        );
    }

    private void loadData() {
        // Load dữ liệu sản phẩm từ service
        productManager.loadProducts(productService.getAllProducts());
    }

    @FXML
    private void toggleCart() {
        animationManager.toggleCart();
    }

    @FXML
    private void closeCart() {
        animationManager.closeCart();
    }
}