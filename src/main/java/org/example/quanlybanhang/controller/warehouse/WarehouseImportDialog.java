package org.example.quanlybanhang.controller.warehouse;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.model.OrderDetail;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.service.ProductService;
import org.example.quanlybanhang.service.UserService;
import org.example.quanlybanhang.service.WarehouseService;
import org.example.quanlybanhang.utils.MoneyUtils;
import org.example.quanlybanhang.utils.TableCellFactoryUtils;
import org.example.quanlybanhang.utils.TextFieldFormatterUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class WarehouseImportDialog {

    private final WarehouseService warehouseService = new WarehouseService();
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();

    private final ObservableList<OrderDetail> productDetailsList = FXCollections.observableArrayList();

    public Label unitPriceLabel;
    public Label totalAmountLabel;


    // --- Thông Tin Giao Dịch ---
    @FXML
    private TextField transactionCodeField;

    @FXML
    private DatePicker createdAtDatePicker;

    @FXML
    private ComboBox<User> createdByComboBox;

    @FXML
    private ComboBox<WarehouseType> transactionTypeComboBox;

    // --- Chi Tiết Sản Phẩm ---
    @FXML
    private ComboBox<Product> productComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField unitPriceField;

    @FXML
    private TableView<OrderDetail> productTableView;

    @FXML
    private TableColumn<OrderDetail, Integer> sttColumn;

    @FXML
    private TableColumn<OrderDetail, String> productIdColumn;

    @FXML
    private TableColumn<OrderDetail, String> productNameColumn;

    @FXML
    private TableColumn<OrderDetail, Integer> quantityColumn;

    @FXML
    private TableColumn<OrderDetail, BigDecimal> unitPriceColumn;

    @FXML
    private TableColumn<OrderDetail, BigDecimal> totalColumn;

    @FXML
    private TableColumn<OrderDetail, Void> actionColumn;

    // --- Thông Tin Bổ Sung ---
    @FXML
    private TextField totalAmountField;

    @FXML
    private TextField excessQuantityField;

    @FXML
    private TextField deficientQuantityField;

    @FXML
    private TextArea noteTextArea;

    @FXML
    private void initialize() {

        transactionCodeField.setEditable(false);

        initializeStaffComboBox();
        initializeTransactionTypeComboBox();   // Phải gọi trước để có selected item
        initializeTransactionInfo();           // Gọi sau khi ComboBox đã được set
        initializeProductComboBox();

        TextFieldFormatterUtils.applyBlazingFastCurrencyFormat(unitPriceField);
        TextFieldFormatterUtils.applyBlazingFastCurrencyFormat(totalAmountField);

        setupProductTableView();
        setupImportTransactionUI();
    }


    private void initializeTransactionInfo() {
        WarehouseType type = transactionTypeComboBox.getValue();
        transactionCodeField.setText(warehouseService.generateTransactionCode(type));
        createdAtDatePicker.setValue(LocalDate.now());
    }

    private void initializeStaffComboBox() {
        createdByComboBox.getItems().addAll(userService.getWarehouseStaffNames());
        createdByComboBox.getSelectionModel().selectFirst();
    }

    private void initializeTransactionTypeComboBox() {
        transactionTypeComboBox.getItems().addAll(WarehouseType.values());
        transactionTypeComboBox.getSelectionModel().selectFirst();

        transactionTypeComboBox.setOnAction(event -> handleTransactionByType());
    }

    @FXML
    private void handleTransactionByType() {
        WarehouseType selectedType = transactionTypeComboBox.getValue();
        if (selectedType == null) return;

        switch (selectedType) {
            case NHAP_KHO -> setupImportTransactionUI();
            case XUAT_KHO -> setupExportTransactionUI();
            case KIEM_KHO -> setupInventoryCheckUI(); // ✅ Gọi kiểm kho
        }
    }

    private void setupInventoryCheckUI() {
        WarehouseType type = transactionTypeComboBox.getValue();
        transactionCodeField.setText(warehouseService.generateTransactionCode(type));
        createdAtDatePicker.setValue(LocalDate.now());
        productDetailsList.clear();
        productTableView.refresh();
        quantityField.clear();
        unitPriceField.clear();
        noteTextArea.clear();

        unitPriceColumn.setVisible(false);
        totalColumn.setVisible(false);

        unitPriceField.setVisible(false);
        unitPriceField.setManaged(false);
        unitPriceLabel.setVisible(false);
        unitPriceLabel.setManaged(false);

        totalAmountLabel.setText("Số Lượng Lỗi");

        totalAmountField.clear();

        // ✅ Cho nhập số lỗi (bỏ trạng thái bị mờ)
        totalAmountField.setDisable(false);
        totalAmountField.setEditable(true);
        totalAmountField.setMouseTransparent(false);
        totalAmountField.setFocusTraversable(true);


        excessQuantityField.setDisable(false);
        deficientQuantityField.setDisable(false);
    }




    private void setupImportTransactionUI() {
        // Reset các field
        WarehouseType type = transactionTypeComboBox.getValue();
        transactionCodeField.setText(warehouseService.generateTransactionCode(type));
        createdAtDatePicker.setValue(LocalDate.now());
        productDetailsList.clear();
        productTableView.refresh();
        totalAmountField.clear();
        quantityField.clear();
        unitPriceField.clear();
        noteTextArea.clear();

        totalAmountLabel.setText("Tổng Tiền");

        // ✅ Hiện lại các cột và trường đã bị ẩn ở xuất kho
        unitPriceColumn.setVisible(true);
        totalColumn.setVisible(true);

        unitPriceField.setVisible(true);
        unitPriceField.setManaged(true);

        unitPriceLabel.setVisible(true);
        unitPriceLabel.setManaged(true);

        totalAmountField.setEditable(false);
        totalAmountField.setFocusTraversable(false);
        totalAmountField.setMouseTransparent(true);

        // Các trường số lượng dư, thiếu sẽ bị disable lại trong nhập kho
        excessQuantityField.setDisable(true);
        deficientQuantityField.setDisable(true);
        totalAmountField.setDisable(false);




    }


    private void setupExportTransactionUI() {
        // Reset dữ liệu giống nhập kho
        WarehouseType type = transactionTypeComboBox.getValue();
        transactionCodeField.setText(warehouseService.generateTransactionCode(type));

        createdAtDatePicker.setValue(LocalDate.now());
        productDetailsList.clear();
        productTableView.refresh();
        totalAmountField.clear();
        quantityField.clear();
        unitPriceField.clear();
        noteTextArea.clear();

        totalAmountLabel.setText("Tổng Tiền");

        unitPriceColumn.setVisible(false);
        totalColumn.setVisible(false);

        unitPriceField.setVisible(false);
        unitPriceLabel.setVisible(false);
        unitPriceLabel.setManaged(false);
        unitPriceField.setManaged(false);

        // ✅ Làm mờ trường tổng tiền (chỉ hiển thị, không chỉnh)
        totalAmountField.setEditable(false);
        totalAmountField.setMouseTransparent(true);
        totalAmountField.setFocusTraversable(false);
        totalAmountField.setDisable(false); // vẫn hiển thị bình thường

        excessQuantityField.setDisable(true);
        deficientQuantityField.setDisable(true);

        quantityField.setDisable(false);
        productComboBox.setDisable(false);
        productTableView.setVisible(true);
    }



    private void initializeProductComboBox() {
        List<Product> products = productService.getAllProducts();
        productComboBox.getItems().addAll(products);
        productComboBox.getSelectionModel().selectFirst();

        productComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        productComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
    }

    private void setupProductTableView() {
        productTableView.setItems(productDetailsList);


        sttColumn.setCellValueFactory(col ->
                new ReadOnlyObjectWrapper<>(productTableView.getItems().indexOf(col.getValue()) + 1)
        );

        productIdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getProductId())));
        productNameColumn.setCellValueFactory(data -> {
            Product product = productService.getProductById(data.getValue().getProductId());
            return new ReadOnlyStringWrapper(product != null ? product.getName() : "N/A");
        });
        quantityColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));


        unitPriceColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrice()));

        unitPriceColumn.setCellFactory(TableCellFactoryUtils.currencyCellFactory());


        totalColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTotal()));
        totalColumn.setCellFactory(TableCellFactoryUtils.currencyCellFactory());


        addRemoveButtonToTable();
    }

    private void addRemoveButtonToTable() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Xóa");

            {
                deleteButton.setOnAction(event -> {
                    OrderDetail item = getTableView().getItems().get(getIndex());
                    productDetailsList.remove(item);
                    updateTotalAmount();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }

    @FXML
    private void onAddProduct() {
        Product product = productComboBox.getValue();
        if (product == null || quantityField.getText().isEmpty()) return;

        int quantity = Integer.parseInt(quantityField.getText().replace(",", ""));
        WarehouseType type = transactionTypeComboBox.getValue();

        OrderDetail existing = productDetailsList.stream()
                .filter(d -> d.getProductId() == product.getId())
                .findFirst().orElse(null);

        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            existing.setQuantity(newQty);
        } else {
            BigDecimal price = BigDecimal.ZERO;
            if (type == WarehouseType.NHAP_KHO) {
                price = MoneyUtils.parseCurrencyText(unitPriceField.getText());
            }

            OrderDetail detail = new OrderDetail(0, 0, product.getId(), quantity, price);
            productDetailsList.add(detail);
        }

        productTableView.refresh();
        updateTotalAmount();

        quantityField.clear();
        unitPriceField.clear();
    }


    private void updateTotalAmount() {
        if (transactionTypeComboBox.getValue() == WarehouseType.XUAT_KHO) {
            totalAmountField.setText("0 đ");
            return;
        }

        BigDecimal total = productDetailsList.stream()
                .map(OrderDetail::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAmountField.setText(MoneyUtils.formatVN(total));
    }


}