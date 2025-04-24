package org.example.dientu99.helpers;

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
            Parent root = FXMLLoader.load(Objects.requireNonNull(LogoutHandler.class.getResource("/org/example/dientu99/views/login/Login.fxml")));
            Stage stage = (Stage) button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
