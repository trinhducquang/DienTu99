package org.example.quanlybanhang.controller.order;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.quanlybanhang.dto.orderDTO.OrderSummaryDTO;
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.service.OrderService;
import org.example.quanlybanhang.utils.MoneyUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class OrderDetailsDialogController {

    @FXML private Label orderId, orderDate, customerName, orderStatus, totalAmount, processedBy;
    @FXML private Label totalProducts, subtotalAmount, shippingFee, finalAmount;
    @FXML private VBox productListContainer;
    @FXML private Button printOrderBtn, updateStatusBtn, backBtn;

    private int currentOrderId;
    private String currentOrderStatus;
    private final OrderService orderService = new OrderService();

    public void initialize() {
        System.out.println("chả có gì =))");
    }

    public void setOrderById(Integer orderId) {
        this.currentOrderId = orderId;
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        OrderSummaryDTO summary = orderService.getOrderSummaryById(currentOrderId);
        if (summary == null) return;

        // Đặt thông tin đơn hàng
        orderId.setText(String.valueOf(summary.id()));
        orderDate.setText(summary.orderDate().toString());
        customerName.setText(summary.customerName());
        currentOrderStatus = summary.status().getText();
        orderStatus.setText(currentOrderStatus);

        // Đặt thông tin giá tiền
        BigDecimal total = summary.totalPrice();
        BigDecimal shipping = summary.shippingFee();
        BigDecimal finalTotal = total.add(shipping);

        totalAmount.setText(MoneyUtils.formatVN(total));
        shippingFee.setText(MoneyUtils.formatVN(shipping));
        finalAmount.setText(MoneyUtils.formatVN(finalTotal));

        // Đặt thông tin người xử lý
        processedBy.setText("ID: " + summary.employeeId());

        // Load danh sách sản phẩm
        loadProductDisplayList();
    }

    private void loadProductDisplayList() {
        List<ProductDisplayInfoDTO> productList = orderService.getProductDisplayInfoList(currentOrderId);
        productListContainer.getChildren().clear();

        int totalItems = 0;
        BigDecimal totalOrderValue = BigDecimal.ZERO;

        for (ProductDisplayInfoDTO product : productList) {
            HBox productBox = createProductBox(
                    product.id(),
                    product.name(),
                    product.imageUrl(),
                    product.quantity(),
                    product.unitPrice(),
                    product.totalPrice()
            );

            totalItems += product.quantity().intValue();
            totalOrderValue = totalOrderValue.add(product.totalPrice());
            productListContainer.getChildren().add(productBox);
        }

        totalProducts.setText(String.valueOf(totalItems));
        subtotalAmount.setText(MoneyUtils.formatVN(totalOrderValue));
    }

    private HBox createProductBox(int productId, String name, String imageUrl, BigDecimal quantity, BigDecimal price, BigDecimal total)
    {
        HBox productBox = new HBox();
        productBox.setSpacing(15);
        productBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #dddddd; -fx-border-radius: 5;");

        VBox detailsBox = new VBox();
        detailsBox.setSpacing(5);

        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblProductId = new Label("Mã sản phẩm: " + productId);
        lblProductId.setStyle("-fx-text-fill: #7f8c8d;");

        Label lblPrice = new Label(MoneyUtils.formatVN(price));
        lblPrice.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label lblQuantity = new Label("Số lượng: " + quantity);
        Label lblTotal = new Label("Thành tiền: " + MoneyUtils.formatVN(total));
        lblTotal.setStyle("-fx-font-weight: bold;");

        detailsBox.getChildren().addAll(lblName, lblProductId, lblQuantity, lblTotal);

        HBox priceBox = new HBox(lblPrice);
        priceBox.setSpacing(10);

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            try {
                ImageView imageView = new ImageView();
                imageView.setFitHeight(80);
                imageView.setFitWidth(80);
                imageView.setPreserveRatio(true);

                if (imageUrl.startsWith("http") || imageUrl.startsWith("file:/")) {
                    imageView.setImage(new Image(imageUrl));
                } else {
                    imageView.setImage(new Image(new File(imageUrl).toURI().toString()));
                }

                productBox.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("Không thể tải hình ảnh: " + imageUrl);
            }
        }

        productBox.getChildren().addAll(detailsBox, priceBox);
        return productBox;
    }

    @FXML
    private void handlePrintOrder() {
        System.out.println("In đơn hàng: " + currentOrderId);
    }

    @FXML
    private void handleUpdateStatus() {
        System.out.println("Cập nhật trạng thái đơn hàng: " + currentOrderId);
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.close();
    }
}
