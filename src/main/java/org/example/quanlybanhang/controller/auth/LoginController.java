package org.example.quanlybanhang.controller.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.example.quanlybanhang.dao.UserDAO;
import org.example.quanlybanhang.utils.DatabaseConnection;
import org.example.quanlybanhang.model.User;

import java.sql.Connection;

public class LoginController {

    @FXML private TextField tenDangNhapField;
    @FXML private PasswordField matKhauField;


    @FXML
    private void xuLyDangNhap() {
        String tenDangNhap = tenDangNhapField.getText();
        String matKhau = matKhauField.getText();

        Connection conn = DatabaseConnection.getConnection();
        UserDAO userDAO = new UserDAO(conn);
        User user = userDAO.dangNhap(tenDangNhap, matKhau);

        if (user != null) {
            switch (user.getRole()) {
                case ADMIN:
                    chuyenScene("Admin.fxml");
                    break;
                case NHAN_VIEN:
                    chuyenScene("Employee.fxml");
                    break;
                default:
                    hienThiThongBao("Lỗi", "Vai trò không hợp lệ!");
            }
        } else {
            hienThiThongBao("Lỗi Đăng Nhập", "Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }


    private void chuyenScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlybanhang/" + fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) tenDangNhapField.getScene().getWindow();

            stage.setScene(new Scene(root));
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