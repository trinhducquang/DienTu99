package org.example.quanlybanhang.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.example.quanlybanhang.dao.WarehouseDAO;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.helpers.NavigatorAdmin;
import org.example.quanlybanhang.helpers.LogoutHandler;

import java.util.*;

public class AdminController {
    @FXML private Pane mainContentPane;
    @FXML private Pane adminContentPane;
    @FXML private Button btnEmployee, btnDashboard, btnProduct, btnOrders, btnCustomers, btnCategory, btnWarehouse, logoutButton;

    // Thêm Label để có thể cập nhật tên sản phẩm bán chạy nhất
    @FXML private Label topSellingProductLabel;

    private final WarehouseDAO warehouseDAO = new WarehouseDAO();

    @FXML
    private void initialize() {
        setupNavigation();
        logoutButton.setOnAction(event -> LogoutHandler.handleLogout(logoutButton));

        // Cập nhật thông tin sản phẩm bán chạy nhất
        updateTopSellingProduct();
    }

    private void setupNavigation() {
        btnDashboard.setOnAction(event -> {
            NavigatorAdmin.navigate(adminContentPane, "admin/Admin.fxml");
            // Cập nhật thông tin sản phẩm bán chạy nhất mỗi khi quay lại màn hình tổng quan
            updateTopSellingProduct();
        });
        btnEmployee.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "employee/employeeManagement.fxml"));
        btnCategory.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "category/Category.fxml"));
        btnProduct.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "product/product.fxml"));
        btnOrders.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "order/order.fxml"));
        btnCustomers.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "customer/customer.fxml"));
        btnWarehouse.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "warehouse/Warehouse.fxml"));
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
}