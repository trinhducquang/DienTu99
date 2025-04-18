package org.example.quanlybanhang.utils;

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImagesUtils {

    // 🧠 Cache ảnh đã tải, sử dụng WeakReference để tránh chiếm dụng bộ nhớ lâu dài
    private static final Map<String, WeakReference<Image>> imageCache = new ConcurrentHashMap<>();

    // 🟩 Phương thức tạo ImageView
    public static ImageView createImageView(String imageUrl, double fitWidth, double fitHeight) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        try {
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                // Kiểm tra cache trước khi tải ảnh
                Image cachedImage = getCachedImage(imageUrl);
                if (cachedImage != null) {
                    imageView.setImage(cachedImage);
                } else {
                    if (imageUrl.startsWith("http") || imageUrl.startsWith("file:/")) {
                        imageView.setImage(new Image(imageUrl));
                    } else {
                        imageView.setImage(new Image(new File(imageUrl).toURI().toString()));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể tải hình ảnh: " + imageUrl);
        }

        return imageView;
    }

    // 🟩 Phương thức cắt ảnh và load background nhanh
    public static ImageView createCroppedImageView(String imageUrl, double sourceWidth, double sourceHeight, double fitWidth, double fitHeight) {
        // Đảm bảo ảnh được cache nếu đã tải trước đó
        String cacheKey = imageUrl + "_" + (int)sourceWidth + "x" + (int)sourceHeight;
        Image cachedImage = getCachedImage(cacheKey);

        // Nếu ảnh đã được cache
        if (cachedImage != null) {
            return createImageViewFromCache(cachedImage, fitWidth, fitHeight);
        }

        // Nếu chưa cache, tải ảnh mới trong background
        ImageView imageView = new ImageView();
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Cắt ảnh theo khuôn hình chữ nhật
        Rectangle clip = new Rectangle(fitWidth, fitHeight);
        imageView.setClip(clip);

        // Tải ảnh trong Task để tránh block UI
        Task<Image> loadTask = new Task<>() {
            @Override
            protected Image call() {
                return new Image(imageUrl, sourceWidth, sourceHeight, true, true);
            }
        };

        loadTask.setOnSucceeded(e -> {
            Image image = loadTask.getValue();
            cacheImage(cacheKey, image);  // Lưu ảnh vào cache
            imageView.setImage(image);

            // Tạo hiệu ứng mượt fade-in khi ảnh load xong
            FadeTransition fade = new FadeTransition(Duration.millis(300), imageView);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        });

        ThreadManager.runBackground(loadTask);
        return imageView;
    }

    // Tạo ImageView từ ảnh đã cache
    private static ImageView createImageViewFromCache(Image image, double fitWidth, double fitHeight) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Cắt ảnh theo khuôn hình chữ nhật
        Rectangle clip = new Rectangle(fitWidth, fitHeight);
        imageView.setClip(clip);

        return imageView;
    }

    // Lấy ảnh từ cache (nếu có)
    private static Image getCachedImage(String key) {
        WeakReference<Image> ref = imageCache.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    // Lưu ảnh vào cache
    private static void cacheImage(String key, Image image) {
        imageCache.put(key, new WeakReference<>(image));
    }
}
