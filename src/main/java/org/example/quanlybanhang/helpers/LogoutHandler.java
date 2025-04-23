package org.example.quanlybanhang.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Objects;

public class LogoutHandler {
    public static void handleLogout(Button button) {
        System.out.println("Logout button clicked!");
        try {
            // Load màn hình đăng nhập
            Parent root = FXMLLoader.load(LogoutHandler.class.getResource("/org/example/quanlybanhang/views/login/Login.fxml"));

            // Lấy stage hiện tại
            Stage stage = (Stage) button.getScene().getWindow();

            // Đổi scene sang đăng nhập
            stage.setScene(new Scene(root));
            stage.centerOnScreen(); // Căn giữa màn hình
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
