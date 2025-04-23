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
import org.example.quanlybanhang.dao.UserDAO;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.security.auth.AuthService;
import org.example.quanlybanhang.security.auth.UserSession;
import org.example.quanlybanhang.utils.AlertUtils;
import org.example.quanlybanhang.utils.DatabaseConnection;
import org.example.quanlybanhang.utils.ThemeManager;


import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button themeButton;

    @FXML
    private ImageView themeIcon;

    private final String VERSION = "1.0.0";

    @FXML
    private void initialize() {
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
            System.err.println("Unable to load theme icon: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), loginButton);
        scaleTransition.setToX(0.95);
        scaleTransition.setToY(0.95);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
        Connection conn = DatabaseConnection.getConnection();
        UserDAO userDAO = new UserDAO(conn);
        AuthService authService = new AuthService(userDAO);
        User user = authService.login(username, password);

        if (user != null) {
            UserSession.setCurrentUser(user);
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), loginButton.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> navigateByRole(user));
            fadeOut.play();
        } else {
            showErrorMessage("Username or password is incorrect");
        }
    }

    private void navigateByRole(User user) {
        switch (user.getRole()) {
            case ADMIN -> changeScene("admin/Admin.fxml");
            case NHAN_VIEN -> changeScene("employee/EmployeeManagement.fxml");
            case BAN_HANG -> changeScene("sales/sales.fxml");
            case NHAN_VIEN_KHO -> changeScene("warehouse/warehouse.fxml");
            case THU_NGAN -> changeScene("order/Order.fxml");
            default -> showErrorMessage("Invalid role!");
        }
    }

    private void changeScene(String fxmlFile) {
        try {
            String fxmlPath = "/org/example/quanlybanhang/views/" + fxmlFile;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Tạo stage mới thay vì dùng stage cũ từ usernameField
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            ThemeManager.applyTheme(scene);

            newStage.setScene(scene);
            newStage.setMaximized(true); // ✅ Phóng to luôn
            newStage.show();

            // Đóng stage cũ (màn hình login)
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorMessage("Unable to change screen: " + e.getMessage());
        }
    }


    private void showErrorMessage(String content) {
        AlertUtils.showError("Login Error", content);
    }

    public String getVersion() {
        return VERSION;
    }
}