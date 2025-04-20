package org.example.quanlybanhang.helpers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.quanlybanhang.controller.interfaces.RefreshableView;
import org.example.quanlybanhang.controller.order.AddOrderDialogController;
import org.example.quanlybanhang.controller.product.ProductDetailDialogController;
import org.example.quanlybanhang.controller.product.ProductDialogController;
import org.example.quanlybanhang.controller.order.OrderDetailsDialogController;
import org.example.quanlybanhang.controller.warehouse.WarehouseImportDialog;
import org.example.quanlybanhang.dto.productDTO.CartItem;
import org.example.quanlybanhang.utils.AlertUtils;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class DialogHelper {

    public static void showDialog(String fxmlPath, String title, Stage ownerStage) {
        showDialog(fxmlPath, title, ownerStage, (RefreshableView) null);
    }

    // Simplified showDialog method to avoid reflection issues
    public static void showDialog(String fxmlPath, String title, Stage ownerStage, RefreshableView parentController) {
        try {
            System.out.println("Bắt đầu mở dialog: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            System.out.println("Đã load controller: " + controller.getClass().getName());

            // Kiểm tra controller cụ thể và trực tiếp gán parentController
            if (controller instanceof ProductDialogController) {
                System.out.println("Đặt parentController cho ProductDialogController");
                ((ProductDialogController) controller).setParentController(parentController);
            } else {
                // Thử sử dụng phương thức setParentController nếu có
                try {
                    Method setParentControllerMethod = controller.getClass().getMethod("setParentController", RefreshableView.class);
                    setParentControllerMethod.invoke(controller, parentController);
                    System.out.println("Đã đặt parentController bằng reflection");
                } catch (Exception e) {
                    System.err.println("Không thể đặt parentController: " + e.getMessage());
                }
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.initModality(Modality.WINDOW_MODAL);

            if (ownerStage != null) {
                dialogStage.initOwner(ownerStage);
            }

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            // Đặt sự kiện close để refresh sau khi dialog đóng
            if (parentController != null) {
                dialogStage.setOnHidden(event -> {
                    System.out.println("Dialog đóng, thực hiện refresh view");
                    // Đảm bảo refresh chạy trên UI thread và sau khi dialog đã đóng hoàn toàn
                    Platform.runLater(() -> {
                        try {
                            System.out.println("Đang thực hiện refresh...");
                            parentController.refresh();
                            System.out.println("Đã refresh xong");
                        } catch (Exception e) {
                            System.err.println("Lỗi khi refresh: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                });
            }

            // Hiển thị dialog và đợi
            System.out.println("Hiển thị dialog và đợi");
            dialogStage.showAndWait();
            System.out.println("Dialog đã đóng");

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể mở dialog: " + e.getMessage());
        }
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
            } else if (controller instanceof WarehouseImportDialog) {
                ((WarehouseImportDialog) controller).setOrderForExport(orderId);
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
        showDialog(fxmlPath, title, ownerStage, controllerInitializer, Modality.WINDOW_MODAL, true);
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
            AlertUtils.showError("Lỗi", "Không thể mở dialog: " + e.getMessage());
        }
    }
}