package org.example.quanlybanhang.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.quanlybanhang.controller.product.ProductDetailDialogController;
import org.example.quanlybanhang.controller.order.OrderDetailsDialogController;

public class DialogHelper {
    public static void showDialog(String fxmlPath, String title, Integer id, String type, Stage ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (id != null) {
                if ("product".equals(type) && controller instanceof ProductDetailDialogController) {
                    ((ProductDetailDialogController) controller).setProductById(id);
                } else if ("order".equals(type) && controller instanceof OrderDetailsDialogController) {
                    ((OrderDetailsDialogController) controller).setOrderById(id);
                }
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.initModality(Modality.NONE);

            if (ownerStage != null) {
                dialogStage.initOwner(ownerStage);
            }

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Overload cho sản phẩm
    public static void showProductDialog(String fxmlPath, String title, Integer productId, Stage ownerStage) {
        showDialog(fxmlPath, title, productId, "product", ownerStage);
    }

    public static void showOrderDialog(String fxmlPath, String title, Integer orderId, Stage ownerStage) {
        showDialog(fxmlPath, title, orderId, "order", ownerStage);
    }

    public static void showDialog(String fxmlPath, String title, Stage ownerStage) {
        showDialog(fxmlPath, title, null, null, ownerStage);
    }
}
