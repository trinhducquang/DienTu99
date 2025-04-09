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
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.*;
import org.example.quanlybanhang.service.*;
import org.example.quanlybanhang.utils.*;

import java.sql.Connection;
import java.time.*;
import java.util.*;

public class AddOrderDialogController {


    //region FXML Components
    @FXML private Button btnAddCustomer;
    @FXML private TextField findProducts, txtOrderId, txtShippingFee, txtTotalPrice, txtFinalTotal;
    @FXML private DatePicker dateOrderDate;
    @FXML private ComboBox<String> cbCustomer;
    @FXML private ComboBox<OrderStatus> cbOrderStatus;
    @FXML private ComboBox<Employee> cbEmployee;
    @FXML private ComboBox<Product> cbProduct;
    @FXML private Spinner<Integer> spQuantity;
    @FXML private TextArea txtNote;
    @FXML private Button btnAddProduct, btnCancel, btnSaveOrder, btnBack;
    @FXML private TableView<OrderDetail> tableOrderDetails;
    @FXML private TableColumn<OrderDetail, Integer> colSTT, colProductId, colQuantity;
    @FXML private TableColumn<OrderDetail, String> colProductName;
    @FXML private TableColumn<OrderDetail, Double> colPrice, colTotal;
    @FXML private TableColumn<OrderDetail, Button> colAction;
    //endregion

    //region Services and Data
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private Map<Integer, String> productMap = new HashMap<>();
    private ObservableList<OrderDetail> orderDetailsList = FXCollections.observableArrayList();
    private List<Product> allProducts = new ArrayList<>();
    private CustomerDAO customerDAO;



    //endregion

    @FXML
    private void initialize() {
        setupServices();
        setupUIComponents();
        setupComboBoxes();
        setupTableView();
        setupSearch();
        setupEvents();
        btnAddCustomer.setOnAction(event -> {
            DialogHelper.showDialog("/org/example/quanlybanhang/CustomerDialog.fxml", "Thêm Khách Hàng Mới");
        });

    }


    private void setupServices() {
        Connection connection = DatabaseConnection.getConnection();
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO(connection);
        customerDAO = new CustomerDAO(); // <-- thêm dòng này
    }

    private void setupUIComponents() {
        txtOrderId.setText(String.valueOf(orderDAO.getNextOrderId()));
        txtOrderId.setDisable(true);
        dateOrderDate.setValue(LocalDate.now());
    }

    private void setupComboBoxes() {
        cbOrderStatus.getItems().setAll(OrderStatus.values());
        loadCustomers();
        loadProducts();
        loadEmployees();
    }

    private void setupSearch() {
        findProducts.textProperty().addListener((obs, oldText, newText) -> {
            List<Product> filtered = SearchService.search(
                    allProducts, newText, Product::getName,
                    product -> String.valueOf(product.getId())
            );
            List<Product> valid = filtered.stream()
                    .filter(p -> p.getStatus() == ProductStatus.CON_HANG)
                    .toList();

            cbProduct.getItems().setAll(valid);
            if (!valid.isEmpty()) cbProduct.setValue(valid.getFirst());
        });
    }

    private void setupEvents() {
        cbProduct.setOnAction(event -> updateSpinnerForProduct(cbProduct.getValue()));
        txtShippingFee.textProperty().addListener((obs, oldVal, newVal) -> updateTotalPrices());
    }

    private void setupTableView() {
        colSTT.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(tableOrderDetails.getItems().indexOf(cell.getValue()) + 1));
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(cell -> new SimpleStringProperty(
                productMap.getOrDefault(cell.getValue().getProductId(), "Không tìm thấy")));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(col -> getCurrencyCell());
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setCellFactory(col -> getCurrencyCell());
        colAction.setCellFactory(col -> getDeleteButtonCell());
        tableOrderDetails.setItems(orderDetailsList);
    }

    private TableCell<OrderDetail, Double> getCurrencyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : MoneyUtils.formatVN(value));
            }
        };
    }

    private TableCell<OrderDetail, Button> getDeleteButtonCell() {
        return new TableCell<>() {
            private final Button btnDelete = new Button("Xóa");
            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    btnDelete.setOnAction(e -> {
                        orderDetailsList.remove(getTableView().getItems().get(getIndex()));
                        updateTotalPrices();
                    });
                    setGraphic(btnDelete);
                }
            }
        };
    }

    private void updateSpinnerForProduct(Product product) {
        if (product == null) return;

        if (product.getStatus() != ProductStatus.CON_HANG) {
            showAlert(Alert.AlertType.WARNING, "Sản phẩm không hợp lệ", "Sản phẩm đã hết hàng hoặc không thể đặt.");
            cbProduct.setValue(null);
            spQuantity.setValueFactory(null);
            return;
        }

        spQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, product.getStockQuantity(), 1));
    }

    @FXML
    private void handleAddProduct() {
        Product product = cbProduct.getValue();
        Integer quantity = spQuantity.getValue();

        if (product == null || quantity == null || quantity <= 0) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng chọn sản phẩm và số lượng hợp lệ!");
            return;
        }

        int availableStock = product.getStockQuantity();
        OrderDetail existing = orderDetailsList.stream()
                .filter(d -> d.getProductId() == product.getId())
                .findFirst().orElse(null);

        int currentQty = existing != null ? existing.getQuantity() : 0;
        int totalQty = currentQty + quantity;

        if (totalQty > availableStock) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Tồn kho chỉ còn " + availableStock + " sản phẩm.");
            return;
        }

        if (existing != null) {
            existing.setQuantity(totalQty);
        } else {
            orderDetailsList.add(new OrderDetail(orderDetailsList.size() + 1,
                    Integer.parseInt(txtOrderId.getText()), product.getId(), quantity, product.getPrice()));
        }

        tableOrderDetails.refresh();
        updateTotalPrices();
        spQuantity.getValueFactory().setValue(1);
    }

    private void updateTotalPrices() {
        double total = orderDetailsList.stream().mapToDouble(d -> d.getQuantity() * d.getPrice()).sum();
        txtTotalPrice.setText(MoneyUtils.formatVN(total));

        try {
            double shippingFee = Double.parseDouble(txtShippingFee.getText());
            txtFinalTotal.setText(MoneyUtils.formatVN(total + shippingFee));
        } catch (NumberFormatException e) {
            txtFinalTotal.setText(MoneyUtils.formatVN(total));
        }
    }

    private void loadProducts() {
        allProducts = productDAO.getAll();
        cbProduct.getItems().clear();
        productMap.clear();

        for (Product p : allProducts) {
            if (p.getStatus() == ProductStatus.CON_HANG) {
                cbProduct.getItems().add(p);
                productMap.put(p.getId(), p.getName());
            }
        }
    }

    private void loadCustomers() {
        cbCustomer.getItems().clear();
        customerDAO.getAll().forEach(c ->
                cbCustomer.getItems().add(c.getId() + " - " + c.getName())
        );
    }


    private void loadEmployees() {
        List<Employee> employees = new EmployeeDAO(DatabaseConnection.getConnection()).getAll();
        cbEmployee.getItems().addAll(employees.stream()
                .filter(e -> UserRole.NHAN_VIEN_KHO.getValue().equalsIgnoreCase(e.getRole()))
                .toList());
    }

    @FXML
    private void handleSaveOrder() {
        try {
            Employee emp = cbEmployee.getValue();
            String cust = cbCustomer.getValue();
            if (emp == null || cust == null || !cust.contains("-")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn khách hàng và nhân viên.");
                return;
            }

            int customerId = Integer.parseInt(cust.split("-")[0].trim());
            double shippingFee = Double.parseDouble(txtShippingFee.getText());
            LocalDateTime orderDate = dateOrderDate.getValue().atStartOfDay();
            List<OrderDetail> details = new ArrayList<>(tableOrderDetails.getItems());

            double totalPrice = details.stream().mapToDouble(d -> d.getQuantity() * d.getPrice()).sum();
            Order order = new Order(0, emp.getId(), customerId, "", totalPrice, shippingFee,
                    orderDate, cbOrderStatus.getValue(), "", txtNote.getText());

            int newId = orderDAO.addOrder(order, details);
            if (newId > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đơn hàng đã được lưu.");
                closeDialogFromEvent(null);
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu đơn hàng.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng kiểm tra lại thông tin.");
        }
    }

    @FXML private void handleCancel(ActionEvent event) { closeDialogFromEvent(event); }
    @FXML private void handleBack(ActionEvent event) { closeDialogFromEvent(event); }

    private void closeDialogFromEvent(ActionEvent event) {
        Stage stage = event != null
                ? (Stage) ((Node) event.getSource()).getScene().getWindow()
                : (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
