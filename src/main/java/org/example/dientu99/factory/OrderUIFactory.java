package org.example.dientu99.factory;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.dientu99.dto.productDTO.ProductDisplayInfoDTO;
import org.example.dientu99.enums.OrderStatus;
import org.example.dientu99.utils.ImagesUtils;
import org.example.dientu99.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class OrderUIFactory {

    private static final Map<String, String> STATUS_STYLE_MAP = new HashMap<>();

    static {
        STATUS_STYLE_MAP.put("đang xử lý", "status-pending");
        STATUS_STYLE_MAP.put("hoàn thành", "status-completed");
        STATUS_STYLE_MAP.put("đã hủy", "status-cancelled");
    }

    /**
     * Tạo box hiển thị thông tin sản phẩm trong chi tiết đơn hàng
     *
     * @param productId ID sản phẩm
     * @param name Tên sản phẩm
     * @param imageUrl URL hình ảnh sản phẩm
     * @param quantity Số lượng
     * @param price Đơn giá
     * @param total Thành tiền
     * @return HBox chứa thông tin sản phẩm
     */
    public static HBox createOrderProductBox(int productId, String name, String imageUrl,
                                             BigDecimal quantity, BigDecimal price, BigDecimal total) {
        HBox productBox = new HBox();
        productBox.setSpacing(15);
        productBox.getStyleClass().add("related-product-item");

        // Phần hình ảnh sản phẩm
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            ImageView imageView = ImagesUtils.createImageView(imageUrl, 80, 80);
            productBox.getChildren().add(imageView);
        }

        // Phần thông tin sản phẩm
        VBox detailsBox = new VBox();
        detailsBox.setSpacing(5);

        Label lblName = new Label(name);
        lblName.getStyleClass().add("related-product-name");

        Label lblProductId = new Label("Mã sản phẩm: " + productId);
        lblProductId.getStyleClass().add("product-id");

        Label lblQuantity = new Label("Số lượng: " + quantity);
        lblQuantity.getStyleClass().add("product-quantity");

        Label lblTotal = new Label("Thành tiền: " + MoneyUtils.formatVN(total));
        lblTotal.getStyleClass().add("spec-value");

        detailsBox.getChildren().addAll(lblName, lblProductId, lblQuantity, lblTotal);

        // Phần giá tiền - góc phải dưới cùng
        StackPane priceContainer = new StackPane();
        HBox.setHgrow(priceContainer, Priority.ALWAYS);

        Label lblPrice = new Label(MoneyUtils.formatVN(price));
        lblPrice.getStyleClass().add("related-product-price");
        StackPane.setAlignment(lblPrice, Pos.BOTTOM_RIGHT);

        priceContainer.getChildren().add(lblPrice);

        // Thêm các phần tử vào box chính
        productBox.getChildren().addAll(detailsBox, priceContainer);

        return productBox;
    }

    /**
     * Tạo box hiển thị từ ProductDisplayInfoDTO
     *
     * @param product Thông tin sản phẩm
     * @return HBox hiển thị sản phẩm
     */
    public static HBox createOrderProductBox(ProductDisplayInfoDTO product) {
        return createOrderProductBox(
                product.id(),
                product.name(),
                product.imageUrl(),
                product.quantity(),
                product.unitPrice(),
                product.totalPrice()
        );
    }

    /**
     * Áp dụng style tương ứng cho trạng thái đơn hàng
     *
     * @param statusLabel Label hiển thị trạng thái
     * @param status Tên trạng thái
     */
    public static void applyOrderStatusStyle(Label statusLabel, String status) {
        statusLabel.getStyleClass().removeAll("status-pending", "status-completed", "status-cancelled");
        String styleClass = STATUS_STYLE_MAP.getOrDefault(status.toLowerCase(), "status-pending");
        statusLabel.getStyleClass().add(styleClass);
    }

    /**
     * Tạo một Label với style và nội dung cho trạng thái đơn hàng
     *
     * @param status Trạng thái đơn hàng
     * @return Label đã định dạng
     */
    public static Label createOrderStatusLabel(OrderStatus status) {
        Label statusLabel = new Label(status.getText());
        applyOrderStatusStyle(statusLabel, status.getText());
        return statusLabel;
    }
}