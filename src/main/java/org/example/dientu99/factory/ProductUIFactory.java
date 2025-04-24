package org.example.dientu99.factory;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.dientu99.model.Product;
import org.example.dientu99.utils.ImagesUtils;
import org.example.dientu99.utils.MoneyUtils;

public class ProductUIFactory {

    // Tạo VBox chứa các phần tử của một sản phẩm
    public static VBox createProductBox(Product product, Runnable onClick) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPrefSize(130, 110);
        box.getStyleClass().add("related-product-item");

        // Thêm ảnh sản phẩm
        ImageView imageView = createCroppedImageView(product.getImageUrl());
        Label name = createCenteredLabel(product.getName(), "related-product-name", 40);
        Label price = createCenteredLabel(MoneyUtils.formatVN(product.getPrice()), "related-product-price", 25);

        box.getChildren().addAll(imageView, name, price);

        // Thêm sự kiện click vào sản phẩm
        box.setOnMouseClicked(e -> onClick.run());

        return box;
    }

    // Tạo ImageView cho ảnh sản phẩm, cắt theo kích thước mong muốn
    private static ImageView createCroppedImageView(String imageUrl) {
        return ImagesUtils.createCroppedImageView(imageUrl, 260, 220, 130, 110);
    }

    // Tạo Label căn giữa với các thuộc tính
    private static Label createCenteredLabel(String text, String styleClass, int height) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setWrapText(true);
        label.setPrefHeight(height);
        label.setAlignment(Pos.CENTER);
        return label;
    }
}
