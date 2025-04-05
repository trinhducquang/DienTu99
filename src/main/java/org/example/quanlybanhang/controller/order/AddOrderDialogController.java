package org.example.quanlybanhang.controller.order;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.Stage;

import org.example.quanlybanhang.dao.*;
import org.example.quanlybanhang.enums.*;
import org.example.quanlybanhang.model.*;
import org.example.quanlybanhang.utils.*;

import java.sql.Connection;
import java.time.*;
import java.util.*;

public class AddOrderDialogController {

    @FXML
    private TextField txtOrderId;
    @FXML
    private DatePicker dateOrderDate;
    @FXML
    private ComboBox<String> cbCustomer;
    @FXML
    private ComboBox<OrderStatus> cbOrderStatus;
    @FXML
    private ComboBox<Employee> cbEmployee;
    @FXML
    private TextArea txtNote;
    @FXML
    private TextField txtShippingFee;
    @FXML
    private TextField txtTotalPrice;
    @FXML
    private ComboBox<Product> cbProduct;
    @FXML
    private Spinner<Integer> spQuantity;
    @FXML
    private Button btnAddProduct;
    @FXML
    private TableView<OrderDetail> tableOrderDetails;
    @FXML
    private TableColumn<OrderDetail, Integer> colSTT;
    @FXML
    private TableColumn<OrderDetail, Integer> colProductId;
    @FXML
    private TableColumn<OrderDetail, String> colProductName;
    @FXML
    private TableColumn<OrderDetail, Integer> colQuantity;
    @FXML
    private TableColumn<OrderDetail, Double> colPrice;
    @FXML
    private TableColumn<OrderDetail, Double> colTotal;
    @FXML
    private TableColumn<OrderDetail, Button> colAction;
    @FXML
    public TextField txtFinalTotal;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSaveOrder;
    @FXML
    private Button btnBack;

    private OrderDAO orderDAO;
    private Map<Integer, String> productMap = new HashMap<>();
    private ProductDAO productDAO;
    private ObservableList<OrderDetail> orderDetailsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        Connection connection = DatabaseConnection.getConnection();
        orderDAO = new OrderDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        ProductDAO productDAO = new ProductDAO(connection);
        cbOrderStatus.getItems().setAll(OrderStatus.values());

        int nextOrderId = orderDAO.getNextOrderId();
        txtOrderId.setText(String.valueOf(nextOrderId));
        txtOrderId.setDisable(true);

        dateOrderDate.setValue(LocalDate.now());

        loadCustomers();
        loadProducts(productDAO);
        loadEmployees();

        cbProduct.setOnAction(event -> {
            Product selectedProduct = cbProduct.getValue();
            if (selectedProduct != null) {
                if (selectedProduct.getStatus() != ProductStatus.CON_HANG &&
                        selectedProduct.getStatus() != ProductStatus.DANG_CHO) {
                    showAlert(Alert.AlertType.WARNING, "Sản phẩm không hợp lệ", "Sản phẩm đã hết hàng hoặc không thể đặt.");
                    cbProduct.setValue(null);
                    spQuantity.setValueFactory(null);
                    return;
                }

                int stockQuantity = selectedProduct.getStockQuantity();
                SpinnerValueFactory<Integer> valueFactory =
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, stockQuantity, 1);
                spQuantity.setValueFactory(valueFactory);
                valueFactory.setWrapAround(true);
            }
        });

        colSTT.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(tableOrderDetails.getItems().indexOf(cellData.getValue()) + 1)
        );

        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));

        colProductName.setCellValueFactory(cellData -> {
            int productId = cellData.getValue().getProductId();
            String productName = productMap.getOrDefault(productId, "Không tìm thấy");
            return new SimpleStringProperty(productName);
        });

        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(MoneyUtils.formatVN(item));
                }
            }
        });

        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(MoneyUtils.formatVN(item));
                }
            }
        });

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Xóa");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    btnDelete.setOnAction(event -> {
                        OrderDetail detail = getTableView().getItems().get(getIndex());
                        orderDetailsList.remove(detail);
                        updateTotalPrices();
                    });
                    setGraphic(btnDelete);
                }
            }
        });

        txtShippingFee.textProperty().addListener((obs, oldVal, newVal) -> updateTotalPrices());
        tableOrderDetails.setItems(orderDetailsList);
    }

    @FXML
    private void handleAddProduct() {
        Product selectedProduct = cbProduct.getValue();
        Integer quantity = spQuantity.getValue();

        if (selectedProduct == null || quantity == null || quantity <= 0) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng chọn sản phẩm và số lượng hợp lệ!");
            return;
        }

        if (selectedProduct.getStatus() != ProductStatus.CON_HANG &&
                selectedProduct.getStatus() != ProductStatus.DANG_CHO) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Sản phẩm không còn hàng hoặc không thể đặt.");
            return;
        }

        OrderDetail existingDetail = orderDetailsList.stream()
                .filter(detail -> detail.getProductId() == selectedProduct.getId())
                .findFirst()
                .orElse(null);

        if (existingDetail != null) {
            existingDetail.setQuantity(existingDetail.getQuantity() + quantity);
        } else {
            OrderDetail detail = new OrderDetail(
                    orderDetailsList.size() + 1,
                    Integer.parseInt(txtOrderId.getText()),
                    selectedProduct.getId(),
                    quantity,
                    selectedProduct.getPrice()
            );
            orderDetailsList.add(detail);
        }

        tableOrderDetails.refresh();
        updateTotalPrices();
    }

    private void updateTotalPrices() {
        double totalPrice = orderDetailsList.stream()
                .mapToDouble(detail -> detail.getQuantity() * detail.getPrice())
                .sum();

        txtTotalPrice.setText(MoneyUtils.formatVN(totalPrice));

        try {
            double shippingFee = Double.parseDouble(txtShippingFee.getText());
            double finalTotal = totalPrice + shippingFee;
            txtFinalTotal.setText(MoneyUtils.formatVN(finalTotal));
        } catch (NumberFormatException e) {
            txtFinalTotal.setText(MoneyUtils.formatVN(totalPrice));
        }
    }

    private void loadProducts(ProductDAO productDAO) {
        List<Product> products = productDAO.getAllProducts();
        cbProduct.getItems().clear();
        productMap.clear();

        for (Product product : products) {
            if (product.getStatus() == ProductStatus.CON_HANG || product.getStatus() == ProductStatus.DANG_CHO) {
                cbProduct.getItems().add(product);
                productMap.put(product.getId(), product.getName());
            }
        }
    }

    private void loadCustomers() {
        List<Customer> customers = CustomerDAO.getAllCustomers();
        cbCustomer.getItems().clear();

        for (Customer customer : customers) {
            cbCustomer.getItems().add(customer.getId() + " - " + customer.getName());
        }
    }

    private void loadEmployees() {
        EmployeeDAO employeeDAO = new EmployeeDAO(DatabaseConnection.getConnection());
        List<Employee> allEmployees = employeeDAO.getAllEmployees();

        List<Employee> filteredEmployees = allEmployees.stream()
                .filter(emp -> "nhanvien".equalsIgnoreCase(emp.getRole()))
                .toList();

        cbEmployee.getItems().clear();
        cbEmployee.getItems().addAll(filteredEmployees);
    }

    @FXML
    private void handleSaveOrder() {
        try {
            Employee selectedEmployee = cbEmployee.getValue();
            if (selectedEmployee == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn nhân viên.");
                return;
            }
            int employeeId = selectedEmployee.getId();

            String customerStr = cbCustomer.getValue();
            if (customerStr == null || !customerStr.contains("-")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn khách hàng hợp lệ.");
                return;
            }
            int customerId = Integer.parseInt(customerStr.split("-")[0].trim());

            double shippingFee = Double.parseDouble(txtShippingFee.getText());
            LocalDateTime orderDate = dateOrderDate.getValue().atStartOfDay();
            OrderStatus status = cbOrderStatus.getValue();
            String note = txtNote.getText();

            List<OrderDetail> orderDetails = new ArrayList<>(tableOrderDetails.getItems());

            double totalPrice = orderDetails.stream()
                    .mapToDouble(detail -> detail.getQuantity() * detail.getPrice())
                    .sum();

            Order newOrder = new Order(0, employeeId, customerId, "", totalPrice, shippingFee, orderDate, status, "", note);

            int orderId = orderDAO.addOrder(newOrder, orderDetails);
            if (orderId > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đơn hàng đã được lưu.");
                closeDialogFromEvent(null);
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu đơn hàng.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeDialogFromEvent(event);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        closeDialogFromEvent(event);
    }

    private void closeDialogFromEvent(ActionEvent event) {
        if (event != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else if (btnCancel != null) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
