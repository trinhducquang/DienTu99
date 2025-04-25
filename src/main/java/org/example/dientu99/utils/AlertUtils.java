package org.example.dientu99.utils;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Optional;

public class AlertUtils {

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyThemeToDialog(alert);
        alert.showAndWait();
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Đã xảy ra lỗi");
        alert.setContentText(message);
        applyThemeToDialog(alert);
        alert.showAndWait();
    }

    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText("Cảnh báo");
        alert.setContentText(message);
        applyThemeToDialog(alert);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("Xác nhận");
        alert.setContentText(message);
        applyThemeToDialog(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Áp dụng theme hiện tại (sáng hoặc tối) cho hộp thoại
     * @param alert Hộp thoại cần áp dụng theme
     */
    private static void applyThemeToDialog(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();

        try {
            Scene tempScene = dialogPane.getScene();
            if (tempScene == null) {
                tempScene = new Scene(dialogPane);
            }
            ThemeManager.applyTheme(tempScene);
            dialogPane.getStyleClass().add("dialog-pane");
        } catch (Exception e) {
            System.err.println("Không thể áp dụng theme cho dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
}