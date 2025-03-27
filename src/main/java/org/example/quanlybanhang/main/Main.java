package org.example.quanlybanhang.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.quanlybanhang.database.DatabaseConnection;

import java.sql.Connection;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/quanlybanhang/Login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quản Lý Bán Hàng");
        primaryStage.show();
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Kết nối thành công!");
        } else {
            System.out.println("Kết nối thất bại!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}