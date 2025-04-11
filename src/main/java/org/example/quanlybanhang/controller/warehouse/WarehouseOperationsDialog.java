package org.example.quanlybanhang.controller.warehouse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.model.OrderDetail;
import org.example.quanlybanhang.service.*;
import org.example.quanlybanhang.utils.MoneyUtils;
import org.example.quanlybanhang.utils.TableCellFactoryUtils;
import org.example.quanlybanhang.utils.TextFieldFormatterUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class WarehouseOperationsDialog {

    private final WarehouseService warehouseService = new WarehouseService();
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();

    private final ObservableList<OrderDetail> productDetailsList = FXCollections.observableArrayList();



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
        totalAmountField.setEditable(false);
        totalAmountField.setFocusTraversable(false);
        totalAmountField.setMouseTransparent(true);
        transactionCodeField.setEditable(false);
        initializeTransactionInfo();
        initializeStaffComboBox();
        initializeTransactionTypeComboBox();
        initializeProductComboBox();



        TextFieldFormatterUtils.applyBlazingFastCurrencyFormat(unitPriceField);
        TextFieldFormatterUtils.applyBlazingFastCurrencyFormat(totalAmountField);


        setupProductTableView();
    }

    private void initializeTransactionInfo() {
        transactionCodeField.setText(warehouseService.generateTransactionCode());
        createdAtDatePicker.setValue(LocalDate.now());
    }

    private void initializeStaffComboBox() {
        createdByComboBox.getItems().addAll(userService.getWarehouseStaffNames());
        createdByComboBox.getSelectionModel().selectFirst();
    }

    private void initializeTransactionTypeComboBox() {
        transactionTypeComboBox.getItems().addAll(WarehouseType.values());
        transactionTypeComboBox.getSelectionModel().selectFirst();
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
        if (product == null || quantityField.getText().isEmpty() || unitPriceField.getText().isEmpty()) return;

        int quantity = Integer.parseInt(quantityField.getText().replace(",", ""));


        BigDecimal price = MoneyUtils.parseCurrencyText(unitPriceField.getText());
//        System.out.println(">> Giá nhập từ TextField: " + unitPriceField.getText());


        // Check if product already exists, update quantity
        OrderDetail existing = productDetailsList.stream()
                .filter(d -> d.getProductId() == product.getId())
                .findFirst().orElse(null);

        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            existing.setQuantity(newQty);
            existing.setPrice(price);
        } else {
            OrderDetail detail = new OrderDetail(0, 0, product.getId(), quantity, price);

            productDetailsList.add(detail);

        }

        productTableView.refresh();
        updateTotalAmount();

        quantityField.clear();
        unitPriceField.clear();
    }

    private void updateTotalAmount() {
        BigDecimal total = productDetailsList.stream()
                .map(OrderDetail::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAmountField.setText(MoneyUtils.formatVN(total));
    }

}