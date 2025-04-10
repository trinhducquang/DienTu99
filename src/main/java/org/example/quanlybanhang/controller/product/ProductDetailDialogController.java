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
import org.example.quanlybanhang.dto.ProductDetailSpecificationsDTO;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.service.ProductService;
import org.example.quanlybanhang.utils.ThreadManager;

import javafx.scene.shape.Rectangle;

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
        relatedProductsContainer.getChildren().clear();

        int offset = currentPage * itemsPerPage;
        List<Product> visibleProducts = productService.getRelatedProducts(product.getCategoryId(), product.getId(), offset, itemsPerPage);

        for (Product p : visibleProducts) {
            VBox productBox = createProductBox(p);
            relatedProductsContainer.getChildren().add(productBox);
        }

        playSlideAnimation(slideFromLeft);
    }


    private VBox createProductBox(Product p) {
        VBox productBox = new VBox();
        productBox.setAlignment(Pos.TOP_CENTER);
        productBox.getStyleClass().add("related-product");
        productBox.setPrefSize(130, 110);

        // 🟩 Load ảnh với kích thước lớn hơn hiển thị
        Image image = new Image(p.getImageUrl(), 260, 220, true, true); // gấp đôi
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(130);
        imageView.setFitHeight(110);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // 🟩 Cắt ảnh đúng kích thước hiển thị (nếu cần)
        Rectangle clip = new Rectangle(130, 110);
        imageView.setClip(clip);

        Label nameLabel = new Label(p.getName());
        nameLabel.getStyleClass().add("related-product-name");
        nameLabel.setWrapText(true);
        nameLabel.setPrefHeight(40);
        nameLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label(String.format("%,.0f VND", p.getPrice()));
        priceLabel.getStyleClass().add("related-product-price");
        priceLabel.setPrefHeight(25);
        priceLabel.setAlignment(Pos.CENTER);

        productBox.getChildren().addAll(imageView, nameLabel, priceLabel);
        productBox.setOnMouseClicked(event -> setProductById(p.getId()));

        return productBox;
    }


    private Image loadProductImageSafe(String url) {
        try {
            String imageUrl = (url != null && !url.isEmpty()) ? url : "/images/default-product.png";
            return new Image(imageUrl, 0, 0, true, false);
        } catch (Exception e) {
            return new Image("/images/default-product.png", 0, 0, true, false);
        }
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
            productImage.setImage(new Image(url, true));
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
