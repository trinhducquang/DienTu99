package org.example.quanlybanhang.controller.login;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.quanlybanhang.controller.ui.animation.UIEffects;
import org.example.quanlybanhang.dao.UserDAO;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.security.auth.AuthService;
import org.example.quanlybanhang.security.auth.UserSession;
import org.example.quanlybanhang.utils.DatabaseConnection;
import org.example.quanlybanhang.utils.ThemeManager;


import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField tenDangNhapField;

    @FXML
    private PasswordField matKhauField;

    @FXML
    private Button dangNhapButton;

    @FXML
    private Button themeButton;

    @FXML
    private ImageView themeIcon;

    private final String VERSION = "1.0.0";

    @FXML
    private void initialize() {
        UIEffects.applyHoverEffect(
                dangNhapButton,
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12px;",
                "-fx-background-color: #1976D2; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12px;"
        );
        UIEffects.applyFocusEffect(tenDangNhapField);
        UIEffects.applyFocusEffect(matKhauField);
        updateThemeIcon();
        themeButton.setOnAction(event -> toggleTheme());
        Platform.runLater(() -> {
            if (themeButton.getScene() != null) {
                ThemeManager.applyTheme(themeButton.getScene());
            }
        });
    }

    private void toggleTheme() {
        Scene scene = themeButton.getScene();
        ThemeManager.toggleTheme(scene);
        updateThemeIcon();
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), themeIcon);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();
    }

    private void updateThemeIcon() {
        String iconPath;
        if (ThemeManager.isDarkMode()) {
            iconPath = "/org/example/quanlybanhang/images/light.png";
        } else {
            iconPath = "/org/example/quanlybanhang/images/dark.png";
        }

        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
            themeIcon.setImage(image);
        } catch (Exception e) {
            System.err.println("Không thể tải icon theme: " + e.getMessage());
        }
    }

    @FXML
    private void xuLyDangNhap() {
        // Existing code remains the same
        String tenDangNhap = tenDangNhapField.getText();
        String matKhau = matKhauField.getText();
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), dangNhapButton);
        scaleTransition.setToX(0.95);
        scaleTransition.setToY(0.95);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
        Connection conn = DatabaseConnection.getConnection();
        UserDAO userDAO = new UserDAO(conn);
        AuthService authService = new AuthService(userDAO);
        User user = authService.login(tenDangNhap, matKhau);

        if (user != null) {
            UserSession.setCurrentUser(user);
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), dangNhapButton.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> chuyenSceneTheoVaiTro(user));
            fadeOut.play();
        } else {
            hienThiThongBaoLoi("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    private void chuyenSceneTheoVaiTro(User user) {
        switch (user.getRole()) {
            case ADMIN -> chuyenScene("admin/Admin.fxml");
            case NHAN_VIEN -> chuyenScene("employee/EmployeeManagement.fxml");
            case BAN_HANG -> chuyenScene("sales/sales.fxml");
            case NHAN_VIEN_KHO -> chuyenScene("warehouse/warehouse.fxml");
            case THU_NGAN -> chuyenScene("order/Order.fxml");
            default -> hienThiThongBaoLoi("Vai trò không hợp lệ!");
        }
    }

    private void chuyenScene(String fxmlFile) {
        try {
            String fxmlPath = "/org/example/quanlybanhang/views/" + fxmlFile;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tenDangNhapField.getScene().getWindow();
            root.setOpacity(0);
            Scene scene = new Scene(root);
            ThemeManager.applyTheme(scene);
            stage.setScene(scene);
            stage.setMaximized(true);

            stage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
                if (!isNowMaximized) {
                    stage.setWidth(1024);
                    stage.setHeight(768);
                    stage.centerOnScreen();
                }
            });

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            hienThiThongBaoLoi("Không thể chuyển màn hình: " + e.getMessage());
        }
    }

    private void hienThiThongBaoLoi(String noiDung) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi Đăng Nhập");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);

        try {
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass()
                    .getResource("/org/example/quanlybanhang/views/css/dialog.css")).toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");
        } catch (Exception e) {
            System.err.println("Không thể tải file CSS: " + e.getMessage());
        }

        alert.showAndWait();
    }

    public String getVersion() {
        return VERSION;
    }
}
