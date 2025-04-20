package org.example.quanlybanhang.controller.order;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.quanlybanhang.dto.orderDTO.OrderSummaryDTO;
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.printing.OrderInvoiceGenerator;
import org.example.quanlybanhang.service.OrderService;
import org.example.quanlybanhang.utils.ImagesUtils;
import org.example.quanlybanhang.utils.MoneyUtils;
import java.math.BigDecimal;
import java.util.List;

public class OrderDetailsDialogController {

    @FXML private Label orderId, orderDate, customerName, totalAmount, processedBy;
    @FXML private Label totalProducts, subtotalAmount, shippingFee, finalAmount;
    @FXML private Label orderStatus; // Giữ nguyên Label này để hiển thị ban đầu
    @FXML private VBox productListContainer;
    @FXML private Button printOrderBtn, updateStatusBtn, backBtn;

    private ComboBox<String> statusComboBox; // ComboBox để chọn trạng thái
    private HBox statusContainer; // Container chứa label và combobox

    private int currentOrderId;
    private String currentOrderStatus;
    private final OrderService orderService = new OrderService();
    private boolean isEditingStatus = false; // Trạng thái đang chỉnh sửa

    public void initialize() {
        // Khởi tạo ComboBox và container
        statusComboBox = new ComboBox<>();
        for (OrderStatus status : OrderStatus.values()) {
            statusComboBox.getItems().add(status.getText());
        }

        // Thêm sự kiện khi chọn giá trị mới
        statusComboBox.setOnAction(e -> saveNewStatus());

        // Tạo container chứa cả label và combobox
        statusContainer = new HBox(10); // Khoảng cách 10px giữa các phần tử
        statusContainer.getChildren().add(new Label("Trạng thái:"));
    }

    public void setOrderById(Integer orderId) {
        this.currentOrderId = orderId;
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        OrderSummaryDTO summary = orderService.getOrderSummaryById(currentOrderId);
        if (summary == null) return;

        // Thiết lập thông tin đơn hàng
        orderId.setText(String.valueOf(summary.id()));
        orderDate.setText(summary.orderDate().toString());
        customerName.setText(summary.customerName());
        currentOrderStatus = summary.status().getText();
        orderStatus.setText(currentOrderStatus);

        // Thiết lập giá trị mặc định cho ComboBox
        statusComboBox.setValue(currentOrderStatus);

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

    private HBox createProductBox(int productId, String name, String imageUrl, BigDecimal quantity, BigDecimal price, BigDecimal total) {
        // Giữ nguyên code như cũ
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
            ImageView imageView = ImagesUtils.createImageView(imageUrl, 80, 80);
            productBox.getChildren().add(imageView);
        }

        productBox.getChildren().addAll(detailsBox, priceBox);
        return productBox;
    }

    @FXML
    private void handlePrintOrder() {
        OrderSummaryDTO summary = orderService.getOrderSummaryById(currentOrderId);
        List<ProductDisplayInfoDTO> productList = orderService.getProductDisplayInfoList(currentOrderId);
        OrderInvoiceGenerator.exportToPdf(summary, productList);
    }

    @FXML
    private void handleUpdateStatus() {
        if (!isEditingStatus) {
            // Chuyển sang chế độ chỉnh sửa: hiển thị ComboBox thay vì Label
            // Lấy parent của orderStatus
            VBox parent = (VBox) orderStatus.getParent();
            // Vị trí của orderStatus trong parent
            int index = parent.getChildren().indexOf(orderStatus);

            // Xóa Label orderStatus khỏi parent
            parent.getChildren().remove(orderStatus);

            // Thêm ComboBox vào vị trí của Label
            statusComboBox.setValue(currentOrderStatus);
            parent.getChildren().add(index, statusComboBox);

            // Đổi text của nút
            updateStatusBtn.setText("Lưu trạng thái");
            isEditingStatus = true;
        } else {
            // Đã nhấn "Lưu trạng thái", lưu trạng thái mới
            saveNewStatus();
        }
    }

    private void saveNewStatus() {
        String newStatus = statusComboBox.getValue();
        if (newStatus != null && !newStatus.equals(currentOrderStatus)) {
            // Cập nhật vào database
            Order order = new Order();
            order.setId(currentOrderId);
            order.setStatus(OrderStatus.fromString(newStatus));

            // Giữ lại note hiện tại
            OrderSummaryDTO summary = orderService.getOrderSummaryById(currentOrderId);
            if (summary != null) {
                order.setNote(summary.note());
            }

            boolean success = orderService.updateOrderStatus(order);

            if (success) {
                // Cập nhật thành công
                currentOrderStatus = newStatus;

                // Chuyển từ ComboBox về Label
                VBox parent = (VBox) statusComboBox.getParent();
                int index = parent.getChildren().indexOf(statusComboBox);
                parent.getChildren().remove(statusComboBox);

                // Cập nhật text cho Label
                orderStatus.setText(newStatus);
                parent.getChildren().add(index, orderStatus);
                updateStatusBtn.setText("Cập nhật trạng thái");
                isEditingStatus = false;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đã cập nhật trạng thái đơn hàng thành công!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Không thể cập nhật trạng thái đơn hàng. Vui lòng thử lại!");
                alert.showAndWait();
            }
        } else if (isEditingStatus) {
            VBox parent = (VBox) statusComboBox.getParent();
            int index = parent.getChildren().indexOf(statusComboBox);
            parent.getChildren().remove(statusComboBox);
            parent.getChildren().add(index, orderStatus);
            updateStatusBtn.setText("Cập nhật trạng thái");
            isEditingStatus = false;
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.close();
    }
}