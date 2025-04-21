package org.example.quanlybanhang.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    private final OrderDAO orderDAO = new OrderDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private Button currentActiveButton;

    @FXML
    private void initialize() {
        setupNavigation();
        logoutButton.setOnAction(event -> LogoutHandler.handleLogout(logoutButton));
        currentActiveButton = btnDashboard;
        updateTopSellingProduct();
        updateCompletedOrdersCount();
        updateAnnualProfit();
    }

    private void updateAnnualProfit() {
        try {
            int currentYear = LocalDateTime.now().getYear();
            BigDecimal annualProfit = orderDAO.calculateAnnualProfit(currentYear);
            if (profitLabel != null) {
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
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("nav-button-active");
        }
        button.getStyleClass().add("nav-button-active");
        currentActiveButton = button;
    }

    private void updateTopSellingProduct() {
        try {
            List<WarehouseDTO> allTransactions = warehouseDAO.getAllWarehouseDetails();
            Map<Integer, WarehouseDTO> productExportMap = new HashMap<>();
            for (WarehouseDTO transaction : allTransactions) {
                if (transaction.getType() == WarehouseType.XUAT_KHO) {
                    int productId = transaction.getProductId();
                    String productName = transaction.getProductName();
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
                    exportSummary.setQuantity(exportSummary.getQuantity() + transaction.getQuantity());
                }
            }
            List<WarehouseDTO> topExportProducts = new ArrayList<>(productExportMap.values());
            topExportProducts.sort((p1, p2) -> Integer.compare(p2.getQuantity(), p1.getQuantity()));
            if (!topExportProducts.isEmpty()) {
                WarehouseDTO topProduct = topExportProducts.getFirst();
                if (topSellingProductLabel != null) {
                    topSellingProductLabel.setText(topProduct.getProductName());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật sản phẩm bán chạy nhất: " + e.getMessage());
            e.printStackTrace();
        }
    }
}