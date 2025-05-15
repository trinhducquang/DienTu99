package org.example.dientu99.controller.product;

import com.google.gson.Gson;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.dientu99.dto.productDTO.ProductDetailSpecificationsDTO;
import org.example.dientu99.factory.ProductUIFactory;
import org.example.dientu99.model.Product;
import org.example.dientu99.service.ProductService;
import org.example.dientu99.utils.MoneyUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProductDetailDialogController {

    @FXML private Label productNameLabel, productIdLabel, categoryLabel, priceLabel, statusLabel, quantityLabel;
    @FXML private Label configMemoryField, cameraDisplayField, batteryField, featuresField, connectivityField, designMaterialsField;
    @FXML private ImageView productImage;
    @FXML private HBox relatedProductsContainer;
    @FXML private Button backButton;

    private final ProductService productService = new ProductService();
    private Product product;
    private static final int ITEMS_PER_PAGE = 7;
    private int totalPages = 0;
    private int currentPage = 0;

    public void setProductById(int productId) {
        showLoadingState();
        CompletableFuture.supplyAsync(() -> productService.getProductById(productId))
                .thenAccept(fetched -> {
                    if (fetched != null) {
                        this.product = fetched;
                        Platform.runLater(this::updateProductDetails);
                        setupRelatedProducts();
                    }
                });
    }

    private void showLoadingState() {
        productNameLabel.setText("Đang tải...");
        productIdLabel.setText("");
        categoryLabel.setText("");
        priceLabel.setText("");
        statusLabel.setText("");
        quantityLabel.setText("");
        productImage.setImage(null);
        setAllSpecsNA();
        relatedProductsContainer.getChildren().clear();
    }

    private void updateProductDetails() {
        if (product == null) return;

        productNameLabel.setText(product.getName());
        productIdLabel.setText("Mã SP: " + product.getId());
        categoryLabel.setText("Danh Mục: " + product.getCategoryName());
        priceLabel.setText(MoneyUtils.formatVN(product.getPrice()));
        statusLabel.setText(product.getStatus() != null ? product.getStatus().toString() : "N/A");
        quantityLabel.setText("Kho: " + product.getStockQuantity());

        loadMainImage(product.getImageUrl());
        loadSpecifications(product.getSpecifications());
    }

    private void loadMainImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            productImage.setImage(null);
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                Image image = new Image(imageUrl, true);
                if (!image.isError()) {
                    Platform.runLater(() -> productImage.setImage(image));
                } else {
                    Platform.runLater(() -> productImage.setImage(null));
                }
            } catch (Exception e) {
                Platform.runLater(() -> productImage.setImage(null));
            }
        });
    }

    private void setupRelatedProducts() {
        CompletableFuture.supplyAsync(() ->
                productService.countRelatedProducts(product.getCategoryId(), product.getId())
        ).thenAccept(totalRelated -> {
            totalPages = (int) Math.ceil((double) totalRelated / ITEMS_PER_PAGE);
            currentPage = 0;
            loadRelatedProducts(false);
        });
    }

    private void loadRelatedProducts(boolean slideFromLeft) {
        int offset = currentPage * ITEMS_PER_PAGE;

        CompletableFuture.supplyAsync(() ->
                productService.getRelatedProducts(product.getCategoryId(), product.getId(), offset, ITEMS_PER_PAGE)
        ).thenAccept(products ->
                Platform.runLater(() -> updateRelatedProductView(products, slideFromLeft))
        );
    }

    private void updateRelatedProductView(List<Product> products, boolean slideFromLeft) {
        relatedProductsContainer.setTranslateX(slideFromLeft ? -800 : 800);
        relatedProductsContainer.getChildren().setAll(
                products.stream()
                        .map(product -> ProductUIFactory.createProductBox(product,
                                () -> setProductById(product.getId())))
                        .toList()
        );
        playSlideAnimation(slideFromLeft);
    }

    private void playSlideAnimation(boolean slideFromLeft) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), relatedProductsContainer);
        transition.setFromX(slideFromLeft ? -800 : 800);
        transition.setToX(0);
        transition.play();
    }

    @FXML private void handlePrevious() {
        if (totalPages == 0) return;
        currentPage = (currentPage - 1 + totalPages) % totalPages;
        loadRelatedProducts(true);
    }

    @FXML private void handleNext() {
        if (totalPages == 0) return;
        currentPage = (currentPage + 1) % totalPages;
        loadRelatedProducts(false);
    }

    private void loadSpecifications(String specifications) {
        if (specifications == null || specifications.isEmpty()) {
            setAllSpecsNA();
            return;
        }

        try {
            ProductDetailSpecificationsDTO specs = new Gson().fromJson(specifications, ProductDetailSpecificationsDTO.class);
            configMemoryField.setText(nullToNA(specs.configMemory()));
            cameraDisplayField.setText(nullToNA(specs.camera()));
            batteryField.setText(nullToNA(specs.battery()));
            featuresField.setText(nullToNA(specs.features()));
            connectivityField.setText(nullToNA(specs.connectivity()));
            designMaterialsField.setText(nullToNA(specs.designMaterials()));
        } catch (Exception e) {
            setAllSpecsNA();
        }
    }

    private void setAllSpecsNA() {
        configMemoryField.setText("N/A");
        cameraDisplayField.setText("N/A");
        batteryField.setText("N/A");
        featuresField.setText("N/A");
        connectivityField.setText("N/A");
        designMaterialsField.setText("N/A");
    }

    private String nullToNA(String value) {
        return (value != null && !value.isBlank()) ? value : "N/A";
    }

    @FXML
    private void handleBackAction() {
        ((Stage) backButton.getScene().getWindow()).close();
    }
}