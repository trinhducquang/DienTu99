package org.example.quanlybanhang.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import java.io.File;

public class ImagesUtils {

    // 🟩 Phương thức tạo ImageView
    public static ImageView createImageView(String imageUrl, double fitWidth, double fitHeight) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        try {
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                if (imageUrl.startsWith("http") || imageUrl.startsWith("file:/")) {
                    imageView.setImage(new Image(imageUrl));
                } else {
                    imageView.setImage(new Image(new File(imageUrl).toURI().toString()));
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể tải hình ảnh: " + imageUrl);
        }

        return imageView;
    }

    // 🟩 Phương thức cắt ảnh
    public static ImageView createCroppedImageView(String imageUrl, double sourceWidth, double sourceHeight, double fitWidth, double fitHeight) {
        // Tải ảnh với kích thước lớn hơn để tránh ảnh bị mờ
        Image image = new Image(imageUrl, sourceWidth, sourceHeight, true, true); // Kích thước lớn hơn để đảm bảo chất lượng
        ImageView imageView = new ImageView(image);

        // Đặt kích thước hiển thị theo yêu cầu
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true); // Giữ tỷ lệ hình ảnh
        imageView.setSmooth(true); // Làm mịn ảnh

        // Cắt ảnh theo kích thước yêu cầu
        Rectangle clip = new Rectangle(fitWidth, fitHeight);
        imageView.setClip(clip); // Cắt ảnh theo khuôn hình chữ nhật

        return imageView;
    }

}
