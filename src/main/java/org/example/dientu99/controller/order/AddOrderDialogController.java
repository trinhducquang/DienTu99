package org.example.dientu99.controller.order;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.dientu99.dao.CustomerDAO;
import org.example.dientu99.dao.EmployeeDAO;
import org.example.dientu99.dao.OrderDAO;
import org.example.dientu99.dao.ProductDAO;
import org.example.dientu99.dto.productDTO.CartItem;
import org.example.dientu99.enums.OrderStatus;
import org.example.dientu99.enums.ProductStatus;
import org.example.dientu99.enums.UserRole;
import org.example.dientu99.helpers.DialogHelper;
import org.example.dientu99.model.*;
import org.example.dientu99.security.auth.UserSession;
import org.example.dientu99.service.SearchService;
import org.example.dientu99.utils.AlertUtils;
import org.example.dientu99.utils.DatabaseConnection;
import org.example.dientu99.utils.TextFieldFormatterUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.dientu99.utils.MoneyUtils.formatVN;
import static org.example.dientu99.utils.TableCellFactoryUtils.currencyCellFactory;

public class AddOrderDialogController {

    @FXML
    private Button btnAddCustomer;
    @FXML
    private TextField findProducts, txtOrderId, txtShippingFee, txtTotalPrice, txtFinalTotal;
    @FXML
    private DatePicker dateOrderDate;
    @FXML
    private ComboBox<String> cbCustomer;
    @FXML
    private ComboBox<OrderStatus> cbOrderStatus;
    @FXML
    private ComboBox<Employee> cbEmployee;
    @FXML
    private ComboBox<Product> cbProduct;
    @FXML
    private Spinner<Integer> spQuantity;
    @FXML
    private TextArea txtNote;
    @FXML
    private Button btnAddProduct, btnCancel, btnSaveOrder, btnBack;
    @FXML
    private TableView<OrderDetail> tableOrderDetails;
    @FXML
    private TableColumn<OrderDetail, Integer> colSTT, colProductId, colQuantity;
    @FXML
    private TableColumn<OrderDetail, String> colProductName;
    @FXML
    private TableColumn<OrderDetail, BigDecimal> colPrice, colTotal;
    @FXML
    private TableColumn<OrderDetail, Button> colAction;
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
            DialogHelper.showDialog("/org/example/dientu99/views/customer/CustomerDialog.fxml", "Thêm Khách Hàng Mới", (Stage) btnAddCustomer.getScene().getWindow());
        });
    }

    private void setupServices() {
        Connection connection = DatabaseConnection.getConnection();
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO(connection);
        customerDAO = new CustomerDAO();
    }

    private void setupUIComponents() {
        txtOrderId.setText(String.valueOf(orderDAO.getNextOrderId()));
        txtOrderId.setDisable(true);
        dateOrderDate.setValue(LocalDate.now());
        TextFieldFormatterUtils.applyBlazingFastCurrencyFormat(txtShippingFee);
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
        colProductName.setCellValueFactory(cell -> new SimpleStringProperty(productMap.getOrDefault(cell.getValue().getProductId(), "Không tìm thấy")));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(currencyCellFactory());

        colTotal.setCellValueFactory(cell -> new SimpleObjectProperty<>(
                cell.getValue().getPrice().multiply(BigDecimal.valueOf(cell.getValue().getQuantity()))
        ));

        colTotal.setCellFactory(currencyCellFactory());

        colAction.setCellFactory(col -> getDeleteButtonCell());
        tableOrderDetails.setItems(orderDetailsList);
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
            AlertUtils.showWarning("Sản phẩm không hợp lệ", "Sản phẩm đã hết hàng hoặc không thể đặt.");
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
            AlertUtils.showWarning("Lỗi", "Vui lòng chọn sản phẩm và số lượng hợp lệ!");
            return;
        }

        int availableStock = product.getStockQuantity();
        OrderDetail existing = orderDetailsList.stream()
                .filter(d -> d.getProductId() == product.getId())
                .findFirst().orElse(null);

        int currentQty = existing != null ? existing.getQuantity() : 0;
        int totalQty = currentQty + quantity;

        if (totalQty > availableStock) {
            AlertUtils.showWarning("Lỗi", "Tồn kho chỉ còn " + availableStock + " sản phẩm.");
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
        BigDecimal total = orderDetailsList.stream()
                .map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        txtTotalPrice.setText(formatVN(total));
        try {
            BigDecimal shippingFee = BigDecimal.ZERO;
            if (txtShippingFee.getText() != null && !txtShippingFee.getText().isEmpty()) {
                shippingFee = TextFieldFormatterUtils.parseCurrencyText(txtShippingFee.getText());
            }

            BigDecimal finalTotal = total.add(shippingFee);
            txtFinalTotal.setText(formatVN(finalTotal));
        } catch (NumberFormatException e) {
            txtFinalTotal.setText(formatVN(total));
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
        Connection connection = DatabaseConnection.getConnection();
        User currentUser = UserSession.getCurrentUser();

        List<Employee> employees = new EmployeeDAO(connection).getAll();

        // Lọc và hiển thị chỉ những nhân viên có vai trò bán hàng
        List<Employee> salesEmployees = employees.stream()
                .filter(e -> e.getRole() == UserRole.BAN_HANG)
                .toList();

        cbEmployee.getItems().clear();
        cbEmployee.getItems().addAll(salesEmployees);

        // Nếu người dùng hiện tại là nhân viên bán hàng, chọn họ trong combobox
        if (currentUser != null && currentUser.getRole() == UserRole.BAN_HANG) {
            // Tìm nhân viên tương ứng với user hiện tại
            for (Employee emp : salesEmployees) {
                if (emp.getId() == currentUser.getId() || emp.getUsername().equals(currentUser.getUsername())) {
                    cbEmployee.setValue(emp);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleSaveOrder() {
        try {
            Employee emp = cbEmployee.getValue();
            String cust = cbCustomer.getValue();
            if (emp == null || cust == null || !cust.contains("-")) {
                AlertUtils.showError("Lỗi", "Vui lòng chọn khách hàng và nhân viên.");
                return;
            }

            int customerId = Integer.parseInt(cust.split("-")[0].trim());
            BigDecimal shippingFee = TextFieldFormatterUtils.parseCurrencyText(txtShippingFee.getText());
            LocalDateTime orderDate = dateOrderDate.getValue().atStartOfDay();
            List<OrderDetail> details = new ArrayList<>(tableOrderDetails.getItems());

            BigDecimal totalPrice = details.stream()
                    .map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Order order = new Order(
                    0,
                    emp.getId(),
                    customerId,
                    "",
                    totalPrice,
                    shippingFee,
                    orderDate,
                    cbOrderStatus.getValue(),
                    "",
                    txtNote.getText()
            );

            int newId = orderDAO.addOrder(order, details);
            if (newId > 0) {
                AlertUtils.showInfo("Thành công", "Đơn hàng đã được lưu.");

                // Không cần gọi refresh ở đây vì DialogHelper đã xử lý
                // mà sẽ thực hiện refresh khi dialog đóng

                closeDialogFromEvent(null);
            } else {
                AlertUtils.showError("Lỗi", "Không thể lưu đơn hàng.");
            }
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Vui lòng kiểm tra lại thông tin.");
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
        Stage stage = event != null
                ? (Stage) ((Node) event.getSource()).getScene().getWindow()
                : (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void setCartItems(ObservableList<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product != null) {
                OrderDetail detail = new OrderDetail(
                        orderDetailsList.size() + 1,
                        Integer.parseInt(txtOrderId.getText()),
                        product.getId(),
                        item.getQuantity().intValue(),
                        product.getPrice()
                );
                orderDetailsList.add(detail);
                productMap.put(product.getId(), product.getName());
            }
        }

        // Cập nhật tổng tiền ngay sau khi thêm các sản phẩm từ giỏ hàng
        updateTotalPrices();
    }
}