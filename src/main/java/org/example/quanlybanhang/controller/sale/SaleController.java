package org.example.quanlybanhang.controller.sale;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.example.quanlybanhang.dao.*;
import org.example.quanlybanhang.dto.productDTO.*;
import org.example.quanlybanhang.model.*;
import org.example.quanlybanhang.utils.*;

import java.math.BigDecimal;
import java.sql.Connection;

import static org.example.quanlybanhang.utils.TextFieldFormatterUtils.formatCurrency;

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

    private static final int ITEMS_PER_PAGE = 12;
    private static final int CART_WIDTH = 300;
    private static final Duration ANIMATION_DURATION = Duration.millis(150);

    private BigDecimal minPrice = BigDecimal.ZERO;
    private BigDecimal maxPrice = new BigDecimal("900000000");

    private Timeline slideInTimeline;
    private Timeline slideOutTimeline;

    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final IntegerProperty cartItemCount = new SimpleIntegerProperty(0);
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);

    @FXML
    public void initialize() {
        initCartSlideAnimation();
        initPriceSliders();
        loadProductsFromDatabase();
        cartItemCount.addListener((obs, oldVal, newVal) -> cartButton.setText("Giỏ hàng (" + newVal + ")"));
    }

    private void initCartSlideAnimation() {
        cartBox.setPrefWidth(CART_WIDTH);
        cartBox.setTranslateX(CART_WIDTH);

        slideInTimeline = new Timeline(
                new KeyFrame(ANIMATION_DURATION, new KeyValue(cartBox.translateXProperty(), 0, Interpolator.EASE_BOTH))
        );

        slideOutTimeline = new Timeline(
                new KeyFrame(ANIMATION_DURATION, new KeyValue(cartBox.translateXProperty(), CART_WIDTH, Interpolator.EASE_BOTH))
        );
        slideOutTimeline.setOnFinished(event -> cartPane.setVisible(false));
    }

    private void initPriceSliders() {
        minPriceSlider.setValue(minPrice.doubleValue());
        maxPriceSlider.setValue(maxPrice.doubleValue());
        updatePriceLabels();

        minPriceSlider.valueProperty().addListener((obs, oldVal, newVal) -> onMinSliderChanged(newVal));
        maxPriceSlider.valueProperty().addListener((obs, oldVal, newVal) -> onMaxSliderChanged(newVal));

        minPriceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) filterProductsByPrice();
        });

        maxPriceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) filterProductsByPrice();
        });
    }

    private void updatePriceLabels() {
        minPriceLabel.setText(MoneyUtils.formatVN(minPrice));
        maxPriceLabel.setText(MoneyUtils.formatVN(maxPrice));
    }

    private void onMinSliderChanged(Number newValue) {
        minPrice = BigDecimal.valueOf(newValue.doubleValue());
        updatePriceLabels();

        if (minPrice.compareTo(BigDecimal.valueOf(maxPriceSlider.getValue())) > 0) {
            maxPriceSlider.setValue(minPrice.doubleValue());
        } else if (!minPriceSlider.isValueChanging()) {
            filterProductsByPrice();
        }
    }

    private void onMaxSliderChanged(Number newValue) {
        maxPrice = BigDecimal.valueOf(newValue.doubleValue());
        updatePriceLabels();

        if (maxPrice.compareTo(BigDecimal.valueOf(minPriceSlider.getValue())) < 0) {
            minPriceSlider.setValue(maxPrice.doubleValue());
        } else if (!maxPriceSlider.isValueChanging()) {
            filterProductsByPrice();
        }
    }

    private void loadProductsFromDatabase() {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            ProductDAO productDAO = new ProductDAO(connection);
            allProducts.setAll(productDAO.getAllProducts());
            filteredProducts.setAll(allProducts);
            setupPagination();
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
    }

    private void setupPagination() {
        PaginationUtils.setup(
                pagination,
                filteredProducts,
                productList,
                currentPage,
                ITEMS_PER_PAGE,
                (pagedData, pageIndex) -> displayProducts()
        );
    }

    private void filterProductsByPrice() {
        filteredProducts.setAll(
                allProducts.filtered(product -> {
                    BigDecimal price = product.getPrice();
                    return price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0;
                })
        );
        resetPagination();
    }

    private void resetPagination() {
        currentPage.set(0);
        pagination.setCurrentPageIndex(0);
        pagination.setPageCount(Math.max(1, (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE)));
        updatePagedItems();
    }

    private void updatePagedItems() {
        int startIndex = currentPage.get() * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredProducts.size());
        productList.setAll(filteredProducts.subList(startIndex, endIndex));
        displayProducts();
    }

    private void displayProducts() {
        gridPane.getChildren().clear();
        int col = 0, row = 0, columns = 6;

        for (Product product : productList) {
            VBox card = createProductCard(product);
            card.setPrefWidth(250);
            gridPane.add(card, col++, row);
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    private void addToCart(ProductDisplayInfoDTO dto) {
        boolean exists = false;
        for (Node node : cartItemsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox itemBox = (VBox) node;
                HBox topBox = (HBox) itemBox.getChildren().get(0);
                Label nameLabel = (Label) ((VBox) topBox.getChildren().get(1)).getChildren().get(0);
                if (nameLabel.getText().equals(dto.name())) {
                    // Sản phẩm đã tồn tại, tăng số lượng
                    TextField quantityField = (TextField) ((HBox) itemBox.getChildren().get(1)).getChildren().get(1);
                    BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
                    currentQuantity = currentQuantity.add(BigDecimal.ONE);
                    quantityField.setText(currentQuantity.toPlainString());
                    updateItemTotal(itemBox, dto.unitPrice(), currentQuantity);
                    exists = true;
                    break;
                }
            }
        }

        if (!exists) {
            // Nếu sản phẩm chưa có trong giỏ, tạo mới và thêm vào
            VBox itemBox = createCartItemBox(dto);
            cartItemsContainer.getChildren().add(itemBox);

            // Cập nhật danh sách CartItem
            CartItem cartItem = new CartItem(new Product(dto.id(), dto.name(), dto.imageUrl(), dto.unitPrice()), dto.quantity());
            cartItems.add(cartItem);
            cartItemCount.set(cartItems.size());
        }

        updateCartTotal();
    }

    private VBox createCartItemBox(ProductDisplayInfoDTO dto) {
        ImageView imageView = new ImageView(new Image(dto.imageUrl()));
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(dto.name());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label priceLabel = new Label(formatCurrency(dto.unitPrice()));
        priceLabel.setStyle("-fx-text-fill: #e74c3c;");

        VBox infoBox = new VBox(nameLabel, priceLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Button deleteBtn = new Button("X");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-min-width: 30;");

        HBox topBox = new HBox(10, imageView, infoBox, deleteBtn);

        // Khởi tạo nút - và +, trường số lượng
        Button minusBtn = new Button("-");
        Button plusBtn = new Button("+");
        TextField quantityField = new TextField(dto.quantity().toPlainString());
        quantityField.setPrefWidth(40);
        quantityField.setAlignment(Pos.CENTER);

        // Tạo label để hiển thị thành tiền
        Label totalItemLabel = new Label(formatCurrency(dto.unitPrice().multiply(dto.quantity())));
        totalItemLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // Xử lý sự kiện khi nhấn nút -
        minusBtn.setOnAction(e -> {
            BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
            if (currentQuantity.compareTo(BigDecimal.ONE) > 0) {
                currentQuantity = currentQuantity.subtract(BigDecimal.ONE);
                quantityField.setText(currentQuantity.toPlainString());
                updateItemTotal((VBox) minusBtn.getParent().getParent(), dto.unitPrice(), currentQuantity);

                // Cập nhật đối tượng CartItem
                for (CartItem item : cartItems) {
                    if (item.getProduct().getId() == dto.id()) {
                        item.decreaseQuantity();
                        break;
                    }
                }

                updateCartTotal();
            }
        });

        // Xử lý sự kiện khi nhấn nút +
        plusBtn.setOnAction(e -> {
            BigDecimal currentQuantity = new BigDecimal(quantityField.getText());
            currentQuantity = currentQuantity.add(BigDecimal.ONE);
            quantityField.setText(currentQuantity.toPlainString());
            updateItemTotal((VBox) plusBtn.getParent().getParent(), dto.unitPrice(), currentQuantity);

            // Cập nhật đối tượng CartItem
            for (CartItem item : cartItems) {
                if (item.getProduct().getId() == dto.id()) {
                    item.increaseQuantity();
                    break;
                }
            }

            updateCartTotal();
        });

        // Xử lý sự kiện khi thay đổi số lượng trực tiếp trong TextField
        quantityField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                quantityField.setText(oldText);
                return;
            }

            try {
                if (!newText.isEmpty()) {
                    BigDecimal newQuantity = new BigDecimal(newText);
                    if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                        quantityField.setText("1");
                        newQuantity = BigDecimal.ONE;
                    }
                    updateItemTotal((VBox) quantityField.getParent().getParent(), dto.unitPrice(), newQuantity);
                    updateCartTotal();
                }
            } catch (NumberFormatException e) {
                quantityField.setText("1");
            }
        });

        HBox quantityBox = new HBox(5, minusBtn, quantityField, plusBtn, new Label("Thành tiền:"), totalItemLabel);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        VBox itemBox = new VBox(5, topBox, quantityBox);
        itemBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: white;");
        itemBox.setPadding(new Insets(10));

        // Xử lý khi nhấn nút X để xóa sản phẩm
        deleteBtn.setOnAction(e -> {
            cartItemsContainer.getChildren().remove(itemBox);

            // Xóa item khỏi danh sách CartItem
            cartItems.removeIf(item -> item.getProduct().getId() == dto.id());
            cartItemCount.set(cartItems.size());

            updateCartTotal();
        });

        return itemBox;
    }

    // Phương thức tính lại thành tiền của một sản phẩm trong giỏ
    private void updateItemTotal(VBox itemBox, BigDecimal unitPrice, BigDecimal quantity) {
        HBox quantityBox = (HBox) itemBox.getChildren().get(1);
        Label totalItemLabel = (Label) quantityBox.getChildren().get(5);
        BigDecimal totalPrice = unitPrice.multiply(quantity);
        totalItemLabel.setText(formatCurrency(totalPrice));
    }

    // Phương thức cập nhật tổng tiền của giỏ hàng
    private void updateCartTotal() {
        BigDecimal total = BigDecimal.ZERO;

        // Tính tổng tiền từ các sản phẩm trong giỏ hàng
        for (Node node : cartItemsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox itemBox = (VBox) node;
                HBox quantityBox = (HBox) itemBox.getChildren().get(1);
                TextField quantityField = (TextField) quantityBox.getChildren().get(1);

                HBox topBox = (HBox) itemBox.getChildren().get(0);
                VBox infoBox = (VBox) topBox.getChildren().get(1);
                Label priceLabel = (Label) infoBox.getChildren().get(1);

                BigDecimal quantity = new BigDecimal(quantityField.getText());
                BigDecimal unitPrice = MoneyUtils.parseCurrencyText(priceLabel.getText());

                total = total.add(unitPrice.multiply(quantity));
            }
        }

        totalLabel.setText(formatCurrency(total));
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white; -fx-padding: 10;");

        ImageView imageView = ImagesUtils.createCroppedImageView(product.getImageUrl(), 260, 220, 220, 160);
        StackPane imagePane = new StackPane(imageView);
        imagePane.setPrefHeight(130);
        imagePane.setStyle("-fx-alignment: center;");

        Label name = new Label(product.getName());
        name.setWrapText(true);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label price = new Label(MoneyUtils.formatVN(product.getPrice()));
        price.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 13;");

        Button addToCart = new Button("Thêm vào giỏ");
        addToCart.setMaxWidth(Double.MAX_VALUE);
        addToCart.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        addToCart.setOnAction(e -> {
            ProductDisplayInfoDTO dto = new ProductDisplayInfoDTO(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    BigDecimal.ONE,
                    product.getPrice(),
                    product.getPrice().multiply(BigDecimal.ONE)
            );
            addToCart(dto);
        });

        VBox.setVgrow(addToCart, Priority.ALWAYS);
        card.getChildren().addAll(imagePane, name, price, addToCart);
        return card;
    }

    @FXML
    private void toggleCart() {
        if (!cartPane.isVisible()) {
            cartPane.setVisible(true);
            slideInTimeline.play();
        } else {
            slideOutTimeline.play();
        }
    }

    @FXML
    private void closeCart() {
        slideOutTimeline.play();
    }
}