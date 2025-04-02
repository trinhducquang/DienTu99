package org.example.quanlybanhang.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import org.example.quanlybanhang.helpers.NavigatorAdmin;
import org.example.quanlybanhang.helpers.LogoutHandler;

public class AdminController {
    @FXML private Pane mainContentPane;
    @FXML private Pane adminContentPane;
    @FXML private Button btnEmployee, btnDashboard, btnProduct, btnOrders, btnCustomers, btnCategory, btnReports, logoutButton;

    @FXML
    private void initialize() {
        setupNavigation();
        logoutButton.setOnAction(event -> LogoutHandler.handleLogout(logoutButton));
    }

    private void setupNavigation() {
        btnDashboard.setOnAction(event -> NavigatorAdmin.navigate(adminContentPane, "Admin.fxml"));
        btnEmployee.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "employeeManagement.fxml"));
        btnCategory.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "Category.fxml"));
        btnProduct.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "product.fxml"));
        btnOrders.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "order.fxml"));
        btnCustomers.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "customer.fxml"));
        btnReports.setOnAction(event -> NavigatorAdmin.navigate(mainContentPane, "report.fxml"));
    }
}
