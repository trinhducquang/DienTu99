package org.example.quanlybanhang.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import org.example.quanlybanhang.helpers.NavigatorAdmin;
import org.example.quanlybanhang.helpers.LogoutHandler;

public class AdminController {
    @FXML private Pane mainContentPane;
    @FXML private Pane adminContentPane;
    @FXML private Button btnEmployee, btnDashboard, btnProduct, btnOrders, btnCustomers, btnCategory, btnWarehouse, logoutButton;

    @FXML
    private void initialize() {
        setupNavigation();
        logoutButton.setOnAction(event -> LogoutHandler.handleLogout(logoutButton));
    }

    private void setupNavigation() {
        btnDashboard.setOnAction(event -> NavigatorAdmin.navigate(adminContentPane, "admin/Admin.fxml"));
        btnEmployee.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "employee/employeeManagement.fxml"));
        btnCategory.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "category/Category.fxml"));
        btnProduct.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "product/product.fxml"));
        btnOrders.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "order/order.fxml"));
        btnCustomers.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "customer/customer.fxml"));
        btnWarehouse.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "warehouse/Warehouse.fxml"));

    }
}
