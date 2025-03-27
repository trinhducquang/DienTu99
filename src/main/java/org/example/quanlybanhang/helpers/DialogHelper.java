package org.example.quanlybanhang.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DialogHelper {
    public static void showDialog(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource(fxmlPath));
            Parent root = loader.load();

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
}
