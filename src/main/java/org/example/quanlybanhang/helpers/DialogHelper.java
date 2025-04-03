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
    public static void showDialog(String fxmlPath, String title, Integer id, String type) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Lấy controller của cửa sổ
            Object controller = loader.getController();

            // Xác định kiểu dữ liệu để truyền vào đúng controller
            if (id != null) {
                if ("product".equals(type) && controller instanceof ProductDetailDialogController) {
                    ((ProductDetailDialogController) controller).setProductById(id);
                } else if ("order".equals(type) && controller instanceof OrderDetailsDialogController) {
                    ((OrderDetailsDialogController) controller).setOrderById(id);
                }
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

    // Overload phương thức cho sản phẩm
    public static void showProductDialog(String fxmlPath, String title, Integer productId) {
        showDialog(fxmlPath, title, productId, "product");
    }

    // Overload phương thức cho đơn hàng
    public static void showOrderDialog(String fxmlPath, String title, Integer orderId) {
        showDialog(fxmlPath, title, orderId, "order");
    }

    // Overload giữ khả năng tương thích ngược
    public static void showDialog(String fxmlPath, String title) {
        showDialog(fxmlPath, title, null, null);
    }
}
