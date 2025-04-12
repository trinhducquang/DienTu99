package org.example.quanlybanhang.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SceneManager {

        public static void loadScene(String fxmlPath, String title, Stage primaryStage) {
            try {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
                StackPane root = loader.load();
                primaryStage.setTitle(title);
                primaryStage.setScene(new Scene(root, 600, 400));
                primaryStage.show();
            } catch (Exception e) {
                e.printStackTrace();
                // Xử lý lỗi nếu không thể tải Scene
            }

    }

}
