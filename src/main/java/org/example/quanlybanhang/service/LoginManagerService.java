package org.example.quanlybanhang.service;

import org.example.quanlybanhang.enums.UserRole;
import org.example.quanlybanhang.enums.UserStatus;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.security.auth.AuthService;
import org.example.quanlybanhang.helpers.SceneManager;


public class LoginManagerService {
    private final AuthService authService;

    public LoginManagerService(AuthService authService) {
        this.authService = authService;
    }

    public void handleLogin(String username, String password, Stage primaryStage) {
        User user = authService.login(username, password);
        if (user == null) {
            showAlert("Sai tài khoản hoặc mật khẩu.");
            return;
        }

        if (user.getStatus() == UserStatus.LOCK) {
            showAlert("Tài khoản của bạn đã bị khóa.");
            return;
        }

        String roleString = user.getRole().toString();

        UserRole role = UserRole.fromString(roleString);
        if (role == null) {
            showAlert("Vai trò không hợp lệ.");
            return;
        }

        // Tải giao diện theo vai trò người dùng
        switch (role) {
            case ADMIN -> SceneManager.loadScene("/fxml/admin-dashboard.fxml", "Quản trị viên", primaryStage);
            case BAN_HANG -> SceneManager.loadScene("/fxml/sale-dashboard.fxml", "Nhân viên bán hàng", primaryStage);
            case THU_NGAN -> SceneManager.loadScene("/fxml/cashier.fxml", "Thu ngân", primaryStage);
            case NHAN_VIEN_KHO -> SceneManager.loadScene("/fxml/warehouse.fxml", "Thủ kho", primaryStage);
            default -> showAlert("Vai trò không hợp lệ.");
        }
    }




    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
