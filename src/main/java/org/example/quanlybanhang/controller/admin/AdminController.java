package org.example.quanlybanhang.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.quanlybanhang.controller.ui.animation.UIEffects;
import org.example.quanlybanhang.dao.OrderDAO;
import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.helpers.LogoutHandler;
import org.example.quanlybanhang.helpers.NavigatorAdmin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class AdminController {
    @FXML
    private Label profitLabel;
    @FXML
    private Label completedOrdersLabel;
    @FXML
    private VBox mainContentPane;
    @FXML
    private VBox adminContentPane;
    @FXML
    private Button btnEmployee, btnDashboard, btnProduct, btnOrders, btnCustomers, btnCategory, btnWarehouse, logoutButton;
    @FXML
    private Label topSellingProductLabel;
    @FXML
    private TableView<?> ordersTable;


    // Version information
    private static final String APP_VERSION = "1.0.0";
    private final OrderDAO orderDAO = new OrderDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private Button currentActiveButton;

    @FXML
    private void initialize() {
        setupNavigation();
        setupUIEffects();
        logoutButton.setOnAction(event -> LogoutHandler.handleLogout(logoutButton));
        currentActiveButton = btnDashboard;
        updateTopSellingProduct();
        updateCompletedOrdersCount();
        updateAnnualProfit();
        setupOrdersTableStatusColumn();
    }

    private void updateAnnualProfit() {
        try {
            int currentYear = LocalDateTime.now().getYear();
            BigDecimal annualProfit = orderDAO.calculateAnnualProfit(currentYear);
            if (profitLabel != null) {
                // Định dạng số để dễ đọc
                java.text.NumberFormat format = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
                profitLabel.setText(format.format(annualProfit));
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật lợi nhuận: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateCompletedOrdersCount() {
        try {
            int completedOrders = orderDAO.countCompletedOrders();
            if (completedOrdersLabel != null) {
                completedOrdersLabel.setText(String.valueOf(completedOrders));
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật số lượng đơn hàng hoàn thành: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupNavigation() {
        btnDashboard.setOnAction(event -> {
            setActiveButton(btnDashboard);
            NavigatorAdmin.navigate(adminContentPane, "admin/Admin.fxml");
            updateTopSellingProduct();
            updateCompletedOrdersCount();
            updateAnnualProfit();
        });

        btnEmployee.setOnAction(event -> {
            setActiveButton(btnEmployee);
            NavigatorAdmin.navigate(mainContentPane, "employee/employeeManagement.fxml");
        });

        btnCategory.setOnAction(event -> {
            setActiveButton(btnCategory);
            NavigatorAdmin.navigate(mainContentPane, "category/Category.fxml");
        });

        btnProduct.setOnAction(event -> {
            setActiveButton(btnProduct);
            NavigatorAdmin.navigate(mainContentPane, "product/product.fxml");
        });

        btnOrders.setOnAction(event -> {
            setActiveButton(btnOrders);
            NavigatorAdmin.navigate(mainContentPane, "order/order.fxml");
        });

        btnCustomers.setOnAction(event -> {
            setActiveButton(btnCustomers);
            NavigatorAdmin.navigate(mainContentPane, "customer/customer.fxml");
        });

        btnWarehouse.setOnAction(event -> {
            setActiveButton(btnWarehouse);
            NavigatorAdmin.navigate(mainContentPane, "warehouse/Warehouse.fxml");
        });
    }

    private void setActiveButton(Button button) {
        // Remove active class from current button
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("nav-button-active");
        }

        // Add active class to new button
        button.getStyleClass().add("nav-button-active");
        currentActiveButton = button;
    }

    private void setupUIEffects() {
        // Apply hover effects to logout button
        String normalLogoutStyle = "-fx-background-color: white; -fx-text-fill: #2196F3; -fx-background-radius: 20; -fx-font-weight: bold;";
        String hoverLogoutStyle = "-fx-background-color: white; -fx-text-fill: #1976D2; -fx-background-radius: 20; -fx-font-weight: bold;";
        UIEffects.applyHoverEffect(logoutButton, normalLogoutStyle, hoverLogoutStyle);

        // Apply hover effects to nav buttons (except the active one)
        for (Button button : Arrays.asList(btnEmployee, btnProduct, btnOrders, btnCustomers, btnCategory, btnWarehouse)) {
            if (button != currentActiveButton) {
                String normalNavStyle = "-fx-background-color: transparent; -fx-text-fill: #212121; -fx-padding: 15 15; -fx-background-radius: 8;";
                String hoverNavStyle = "-fx-background-color: #E0E0E0; -fx-text-fill: #212121; -fx-padding: 15 15; -fx-background-radius: 8;";
                UIEffects.applyHoverEffect(button, normalNavStyle, hoverNavStyle);
            }
        }
    }

    private void updateTopSellingProduct() {
        try {
            // Lấy tất cả các giao dịch kho
            List<WarehouseDTO> allTransactions = warehouseDAO.getAllWarehouseDetails();

            // Tạo map để theo dõi số lượng xuất kho cho từng sản phẩm
            Map<Integer, WarehouseDTO> productExportMap = new HashMap<>();

            // Thống kê số lượng xuất kho cho từng sản phẩm
            for (WarehouseDTO transaction : allTransactions) {
                if (transaction.getType() == WarehouseType.XUAT_KHO) {
                    int productId = transaction.getProductId();
                    String productName = transaction.getProductName();

                    // Tạo hoặc cập nhật bản ghi cho sản phẩm
                    WarehouseDTO exportSummary = productExportMap.computeIfAbsent(
                            productId,
                            k -> {
                                WarehouseDTO dto = new WarehouseDTO();
                                dto.setProductId(productId);
                                dto.setProductName(productName);
                                dto.setQuantity(0);
                                return dto;
                            }
                    );

                    // Cộng dồn số lượng xuất kho
                    exportSummary.setQuantity(exportSummary.getQuantity() + transaction.getQuantity());
                }
            }

            // Chuyển Map thành List để sắp xếp
            List<WarehouseDTO> topExportProducts = new ArrayList<>(productExportMap.values());

            // Sắp xếp theo số lượng xuất kho giảm dần
            topExportProducts.sort((p1, p2) -> Integer.compare(p2.getQuantity(), p1.getQuantity()));

            // Lấy sản phẩm bán chạy nhất (sản phẩm đầu tiên trong danh sách đã sắp xếp)
            if (!topExportProducts.isEmpty()) {
                WarehouseDTO topProduct = topExportProducts.get(0);
                // Cập nhật tên sản phẩm bán chạy nhất vào Label
                if (topSellingProductLabel != null) {
                    topSellingProductLabel.setText(topProduct.getProductName());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật sản phẩm bán chạy nhất: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void setupOrdersTableStatusColumn() {
        // This is a placeholder - you'll need to adapt this to your actual table structure
        if (ordersTable != null && ordersTable.getColumns().size() >= 5) {
            TableColumn<Object, String> statusColumn = (TableColumn<Object, String>) ordersTable.getColumns().get(4);

            statusColumn.setCellFactory(column -> new TableCell<Object, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    getStyleClass().removeAll("status-pending", "status-completed", "status-cancelled");

                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);

                        if (item.equalsIgnoreCase("Đang xử lý") || item.equalsIgnoreCase("Pending")) {
                            getStyleClass().add("status-pending");
                        } else if (item.equalsIgnoreCase("Hoàn thành") || item.equalsIgnoreCase("Completed")) {
                            getStyleClass().add("status-completed");
                        } else if (item.equalsIgnoreCase("Đã hủy") || item.equalsIgnoreCase("Cancelled")) {
                            getStyleClass().add("status-cancelled");
                        }
                    }
                }
            });
        }
    }

    public static String getAppVersion() {
        return APP_VERSION;
    }
}