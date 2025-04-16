package org.example.quanlybanhang.controller.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.UserDAO;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.security.auth.AuthService;
import org.example.quanlybanhang.security.auth.UserSession;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;

import static org.example.quanlybanhang.enums.UserRole.*;

public class LoginController {

    @FXML private TextField tenDangNhapField;
    @FXML private PasswordField matKhauField;

    @FXML
    private void xuLyDangNhap() {
        String tenDangNhap = tenDangNhapField.getText();
        String matKhau = matKhauField.getText();

        Connection conn = DatabaseConnection.getConnection();
        UserDAO userDAO = new UserDAO(conn);
        AuthService authService = new AuthService(userDAO);
        User user = authService.login(tenDangNhap, matKhau);

        if (user != null) {
            // Lưu thông tin người dùng đăng nhập
            UserSession.setCurrentUser(user);

            switch (user.getRole()) {
                case ADMIN:
                    chuyenScene("admin/Admin.fxml");
                    break;
                case NHAN_VIEN:
                    chuyenScene("employee/Employee.fxml");
                    break;
                case BAN_HANG:
                    chuyenScene("sales/sales.fxml");
                    break;
                case NHAN_VIEN_KHO:
                    chuyenScene("warehouse/warehouse.fxml");
                    break;
                case THU_NGAN:
                    chuyenScene("order/Order.fxml");
                    break;
                default:
                    hienThiThongBao("Lỗi", "Vai trò không hợp lệ!");
                    break;
            }
        } else {
            hienThiThongBao("Lỗi Đăng Nhập", "Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }



    private void chuyenScene(String fxmlFile) {
        try {
            // Chọn FXML phù hợp với vai trò người dùng
            String fxmlPath = "/org/example/quanlybanhang/views/" + fxmlFile;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tenDangNhapField.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
                if (!isNowMaximized) {
                    stage.setWidth(1280);
                    stage.setHeight(720);
                    stage.centerOnScreen();
                }
            });
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi", "Không thể chuyển màn hình");
        }
    }


    private void hienThiThongBao(String tieuDe, String noiDung) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}
