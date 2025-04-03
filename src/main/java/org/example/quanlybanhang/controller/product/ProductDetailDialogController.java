package org.example.quanlybanhang.controller.product;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.dao.ProductDAO;  // Import ProductDAO
import org.example.quanlybanhang.utils.DatabaseConnection;  // Import DatabaseConnection
import java.sql.Connection;

public class ProductDetailDialogController {

    @FXML
    private Label productNameLabel;
    @FXML
    private Label productIdLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label quantityLabel;
    @FXML
    private ImageView productImage;
    @FXML
    private Label configMemoryLabel;
    @FXML
    private Label cameraDisplayLabel;
    @FXML
    private Label batteryLabel;
    @FXML
    private Label featuresLabel;
    @FXML
    private Label connectivityLabel;
    @FXML
    private Label designMaterialsLabel;
    @FXML
    private Button backButton;

    private Product product;
    private ProductDAO productDAO;  // Declare ProductDAO

    // Constructor to initialize ProductDAO with database connection
    public ProductDetailDialogController() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            productDAO = new ProductDAO(connection);
        } catch (Exception e) {
            System.err.println("Error: Could not establish database connection - " + e.getMessage());
        }
    }

    public void setProductById(int productId) {
        // Use ProductDAO to fetch the product by ID
        this.product = productDAO.getProductById(productId);
        if (product != null) {
            updateProductDetails();
        } else {
            System.err.println("Error: Product not found!");
        }
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

        // Load product image
        loadProductImage(product.getImageUrl());

        // Load specifications
        loadSpecifications(product.getSpecifications());
    }

    private void loadProductImage(String imageUrl) {
        String defaultImagePath = "/images/default-product.png";
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                productImage.setImage(new Image(imageUrl, true));
            } else {
                productImage.setImage(new Image(defaultImagePath));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            productImage.setImage(new Image(defaultImagePath));
        }
    }

    private void loadSpecifications(String specifications) {
        if (specifications == null || specifications.isEmpty()) {
            configMemoryLabel.setText("N/A");
            cameraDisplayLabel.setText("N/A");
            batteryLabel.setText("N/A");
            featuresLabel.setText("N/A");
            connectivityLabel.setText("N/A");
            designMaterialsLabel.setText("N/A");
            return;
        }

        String[] specs = specifications.split(";");
        configMemoryLabel.setText(specs.length > 0 ? specs[0] : "N/A");
        cameraDisplayLabel.setText(specs.length > 1 ? specs[1] : "N/A");
        batteryLabel.setText(specs.length > 2 ? specs[2] : "N/A");
        featuresLabel.setText(specs.length > 3 ? specs[3] : "N/A");
        connectivityLabel.setText(specs.length > 4 ? specs[4] : "N/A");
        designMaterialsLabel.setText(specs.length > 5 ? specs[5] : "N/A");
    }

    @FXML
    private void handleBackAction() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
