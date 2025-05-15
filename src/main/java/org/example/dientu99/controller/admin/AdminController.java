package org.example.dientu99.controller.admin;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.example.dientu99.dao.OrderDAO;
import org.example.dientu99.dao.UserDAO;
import org.example.dientu99.dao.WarehouseDAO;
import org.example.dientu99.dto.EmployeeDTO.TopEmployee;
import org.example.dientu99.dto.warehouseDTO.WarehouseDTO;
import org.example.dientu99.enums.WarehouseType;
import org.example.dientu99.helpers.LogoutHandler;
import org.example.dientu99.helpers.NavigatorAdmin;
import org.example.dientu99.utils.DatabaseConnection;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.example.dientu99.utils.MoneyUtils.formatVN;

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
    private Label revenueLabel;
    @FXML
    private TableView<TopEmployee> ordersTable;
    @FXML
    private TableColumn<TopEmployee, Integer> idColumn;
    @FXML
    private TableColumn<TopEmployee, String> nameColumn;
    @FXML
    private TableColumn<TopEmployee, Integer> totalOrdersColumn;
    @FXML
    private TableColumn<TopEmployee, String> totalRevenueColumn;
    @FXML
    private TableColumn<TopEmployee, String> totalProfitColumn;


    // Version information
    private final OrderDAO orderDAO = new OrderDAO();
    private final WarehouseDAO warehouseDAO = new WarehouseDAO();
    private final UserDAO userDAO = new UserDAO(DatabaseConnection.getConnection());
    private Button currentActiveButton;

    @FXML
    private void initialize() {
        setupNavigation();
        setupTableColumns();
        logoutButton.setOnAction(event -> LogoutHandler.handleLogout(logoutButton));
        currentActiveButton = btnDashboard;
        updateTopSellingProduct();
        updateAnnualProfit();
        updateCompletedOrdersThisMonth();
        updateTotalRevenue();
        updateDashboardData();
    }

    private void updateDashboardData() {
        updateTopSellingProduct();
        updateAnnualProfit();
        updateCompletedOrdersThisMonth();
        updateTotalRevenue();
        updateTopEmployees();
    }

    private void updateTopEmployees() {
        try {
            List<Map<String, Object>> topEmployeesList = userDAO.getTopSalesEmployees(5);

            ObservableList<TopEmployee> employees = FXCollections.observableArrayList();
            for (Map<String, Object> data : topEmployeesList) {
                employees.add(new TopEmployee(
                        (Integer) data.get("id"),
                        (String) data.get("fullName"),
                        (Integer) data.get("totalOrders"),
                        (BigDecimal) data.get("totalRevenue"),
                        (BigDecimal) data.get("totalProfit")
                ));
            }

            ordersTable.setItems(employees);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật danh sách nhân viên xuất sắc: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().id()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().fullName()));
        totalOrdersColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().totalOrders()).asObject());

        // Định dạng tiền tệ cho totalRevenueColumn và totalProfitColumn
        totalRevenueColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatVN(cellData.getValue().totalRevenue()))
        );
        totalProfitColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatVN(cellData.getValue().totalProfit()))
        );
    }




    private void updateCompletedOrdersThisMonth() {
        try {
            int completedOrders = orderDAO.countCompletedOrdersInCurrentMonth();

            if (completedOrdersLabel != null) {
                completedOrdersLabel.setText(String.valueOf(completedOrders));
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật số đơn hàng hoàn thành: " + e.getMessage());
            e.printStackTrace();
        }
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

    private void updateTotalRevenue() {
        try {
            int currentYear = LocalDateTime.now().getYear();
            BigDecimal totalRevenue = orderDAO.calculateTotalRevenue(currentYear);
            if (revenueLabel != null) {
                java.text.NumberFormat format = java.text.NumberFormat.getInstance(new Locale("vi", "VN"));
                revenueLabel.setText(format.format(totalRevenue));
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật tổng doanh thu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupNavigation() {
        btnDashboard.setOnAction(event -> {
            setActiveButton(btnDashboard);
            NavigatorAdmin.navigate(adminContentPane, "admin/Admin.fxml");
            updateTopSellingProduct();
            updateAnnualProfit();
            updateDashboardData();
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