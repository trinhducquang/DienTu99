package org.example.quanlybanhang.controller.product;

import com.google.gson.Gson;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.quanlybanhang.dto.productDTO.ProductDetailSpecificationsDTO;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.service.ProductService;
import org.example.quanlybanhang.utils.ImagesUtils;
import org.example.quanlybanhang.utils.MoneyUtils;
import org.example.quanlybanhang.utils.ThreadManager;
import java.util.List;

public class ProductDetailDialogController {

    @FXML
    private Label productNameLabel, productIdLabel, categoryLabel, priceLabel, statusLabel, quantityLabel;
    @FXML
    private Label configMemoryField, cameraDisplayField, batteryField, featuresField, connectivityField, designMaterialsField;
    @FXML
    private ImageView productImage;
    @FXML
    private HBox relatedProductsContainer;
    @FXML
    private Button backButton;

    private final ProductService productService = new ProductService(); // sử dụng service
    private Product product;

    private int totalRelatedCount = 0;
    private int totalPages = 0;
    private int currentPage = 0;
    private final int itemsPerPage = 7;

    public void setProductById(int productId) {
        // Run in background thread
        ThreadManager.runBackground(() -> {
            Product productData = productService.getProductById(productId);
            if (productData != null) {
                ThreadManager.runOnUiThread(() -> {
                    this.product = productData;
                    updateProductDetails();
                    loadRelatedProducts(product.getCategoryId(), product.getId());
                });
            } else {
                System.err.println("Error: Product not found!");
            }
        });
    }

    private void loadRelatedProducts(int categoryId, int excludedId) {
        currentPage = 0;
        totalRelatedCount = productService.countRelatedProducts(categoryId, excludedId);
        totalPages = (int) Math.ceil((double) totalRelatedCount / itemsPerPage);
        updateRelatedProductsView(false);
    }

    private void updateRelatedProductsView(boolean slideFromLeft) {
        relatedProductsContainer.setTranslateX(slideFromLeft ? -800 : 800);

        // Use the same list and update only visible products for faster rendering
        int offset = currentPage * itemsPerPage;
        List<Product> visibleProducts = productService.getRelatedProducts(product.getCategoryId(), product.getId(), offset, itemsPerPage);

        relatedProductsContainer.getChildren().clear();  // Only clear content once

        for (Product p : visibleProducts) {
            VBox productBox = createProductBox(p);
            relatedProductsContainer.getChildren().add(productBox);
        }

        playSlideAnimation(slideFromLeft);
    }

    private VBox createProductBox(Product p) {
        VBox productBox = new VBox();
        productBox.getStyleClass().add("related-product-item"); // Thêm class CSS
        productBox.setAlignment(Pos.TOP_CENTER);
        productBox.setPrefSize(130, 110);
        productBox.setSpacing(5); // Thêm khoảng cách giữa các phần tử

        // Load images efficiently with ImagesUtils
        ImageView imageView = ImagesUtils.createCroppedImageView(p.getImageUrl(), 260, 220, 130, 110);

        Label nameLabel = new Label(p.getName());
        nameLabel.getStyleClass().add("related-product-name");
        nameLabel.setWrapText(true);
        nameLabel.setPrefHeight(40);
        nameLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label(MoneyUtils.formatVN(p.getPrice()));
        priceLabel.getStyleClass().add("related-product-price");
        priceLabel.setPrefHeight(25);
        priceLabel.setAlignment(Pos.CENTER);

        productBox.getChildren().addAll(imageView, nameLabel, priceLabel);
        productBox.setOnMouseClicked(event -> setProductById(p.getId()));

        return productBox;
    }

    private void playSlideAnimation(boolean slideFromLeft) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), relatedProductsContainer);
        transition.setFromX(slideFromLeft ? -800 : 800);
        transition.setToX(0);
        transition.play();
    }

    @FXML
    private void handlePrevious() {
        if (totalPages == 0) return;
        currentPage = (currentPage - 1 + totalPages) % totalPages;
        updateRelatedProductsView(true);
    }

    @FXML
    private void handleNext() {
        if (totalPages == 0) return;
        currentPage = (currentPage + 1) % totalPages;
        updateRelatedProductsView(false);
    }

    private void updateProductDetails() {
        if (product == null) {
            System.err.println("Error: No product data available!");
            return;
        }

        productNameLabel.setText(product.getName());
        productIdLabel.setText("Mã SP: " + product.getId());
        categoryLabel.setText("Danh Mục: " + product.getCategoryName());
        priceLabel.setText(String.format("%,.0f VND", product.getPrice()));
        statusLabel.setText(product.getStatus() != null ? product.getStatus().toString() : "N/A");
        quantityLabel.setText("Kho: " + product.getStockQuantity());

        loadMainImage(product.getImageUrl());
        loadSpecifications(product.getSpecifications());
    }

    private void loadMainImage(String imageUrl) {
        try {
            String url = (imageUrl != null && !imageUrl.isEmpty()) ? imageUrl : "/images/default-product.png";
            productImage.setImage(new Image(url, true)); // Load nhanh ảnh chính
        } catch (Exception e) {
            productImage.setImage(new Image("/images/default-product.png"));
        }
    }

    private void loadSpecifications(String specifications) {
        if (specifications == null || specifications.isEmpty()) {
            setAllSpecsNA();
            return;
        }

        Gson gson = new Gson();
        ProductDetailSpecificationsDTO specsDTO = gson.fromJson(specifications, ProductDetailSpecificationsDTO.class);

        configMemoryField.setText(nullToNA(specsDTO.configMemory()));
        cameraDisplayField.setText(nullToNA(specsDTO.camera()));
        batteryField.setText(nullToNA(specsDTO.battery()));
        featuresField.setText(nullToNA(specsDTO.features()));
        connectivityField.setText(nullToNA(specsDTO.connectivity()));
        designMaterialsField.setText(nullToNA(specsDTO.designMaterials()));
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
        return (value != null) ? value : "N/A";
    }

    @FXML
    private void handleBackAction() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
