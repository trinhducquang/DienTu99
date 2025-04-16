package org.example.quanlybanhang.helpers;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.quanlybanhang.controller.order.AddOrderDialogController;
import org.example.quanlybanhang.controller.product.ProductDetailDialogController;
import org.example.quanlybanhang.controller.order.OrderDetailsDialogController;
import org.example.quanlybanhang.dto.productDTO.CartItem;

import java.util.function.Consumer;

public class DialogHelper {

    public static void showDialog(String fxmlPath, String title, Stage ownerStage) {
        showDialog(fxmlPath, title, ownerStage, null);
    }

    public static void showProductDialog(String fxmlPath, String title, Integer productId, Stage ownerStage) {
        showDialog(fxmlPath, title, ownerStage, controller -> {
            if (controller instanceof ProductDetailDialogController) {
                ((ProductDetailDialogController) controller).setProductById(productId);
            }
        });
    }

    public static void showOrderDialog(String fxmlPath, String title, Integer orderId, Stage ownerStage) {
        showDialog(fxmlPath, title, ownerStage, controller -> {
            if (controller instanceof OrderDetailsDialogController) {
                ((OrderDetailsDialogController) controller).setOrderById(orderId);
            }
        });
    }

    public static void showOrderCreationDialog(String fxmlPath, String title, ObservableList<CartItem> cartItems, Stage ownerStage) {
        showDialog(fxmlPath, title, ownerStage, controller -> {
            if (controller instanceof AddOrderDialogController) {
                ((AddOrderDialogController) controller).setCartItems(cartItems);
            }
        }, Modality.APPLICATION_MODAL, true);
    }

    private static void showDialog(String fxmlPath, String title, Stage ownerStage,
                                   Consumer<Object> controllerInitializer) {
        showDialog(fxmlPath, title, ownerStage, controllerInitializer, Modality.NONE, false);
    }

    private static void showDialog(String fxmlPath, String title, Stage ownerStage,
                                   Consumer<Object> controllerInitializer,
                                   Modality modality, boolean waitForClose) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controllerInitializer != null) {
                controllerInitializer.accept(controller);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.initModality(modality);

            if (ownerStage != null) {
                dialogStage.initOwner(ownerStage);
            }

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            if (waitForClose) {
                dialogStage.showAndWait();
            } else {
                dialogStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}