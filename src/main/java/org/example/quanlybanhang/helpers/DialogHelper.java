package org.example.quanlybanhang.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.quanlybanhang.controller.ProductDetailDialogController;

public class DialogHelper {
    public static void showDialog(String fxmlPath, String title, Integer productId) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Lấy controller của cửa sổ
            Object controller = loader.getController();

            // Nếu controller là ProductDetailDialogController và có productId thì truyền vào
            if (productId != null && controller instanceof ProductDetailDialogController) {
                ((ProductDetailDialogController) controller).setProductById(productId);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Overload phương thức để giữ khả năng tương thích ngược
    public static void showDialog(String fxmlPath, String title) {
        showDialog(fxmlPath, title, null);
    }
}
