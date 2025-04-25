package org.example.dientu99.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.dientu99.utils.DatabaseConnection;
import org.example.dientu99.utils.ThreadManager;
import java.sql.Connection;
import java.util.Objects;


public class    Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/dientu99/views/login/Login.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Điện tử 99");
        primaryStage.show();
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Kết nối thành công!");
        } else {
            System.out.println("Kết nối thất bại!");
        }
    }

    public void stop() {
        System.out.println("Ứng dụng đang tắt, giải phóng tài nguyên...");
        ThreadManager.shutdown();
        System.out.println("Đã shutdown thread pool.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}