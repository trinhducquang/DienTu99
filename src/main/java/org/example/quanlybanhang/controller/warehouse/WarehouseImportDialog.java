package org.example.quanlybanhang.controller.warehouse;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.quanlybanhang.dto.warehouseDTO.WarehouseDTO;
import org.example.quanlybanhang.enums.WarehouseType;
import org.example.quanlybanhang.model.OrderDetail;
import org.example.quanlybanhang.model.Product;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.service.ProductService;
import org.example.quanlybanhang.service.UserService;
import org.example.quanlybanhang.service.WarehouseService;
import org.example.quanlybanhang.utils.AlertUtils;
import org.example.quanlybanhang.utils.MoneyUtils;
import org.example.quanlybanhang.utils.TableCellFactoryUtils;
import org.example.quanlybanhang.utils.TextFieldFormatterUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class WarehouseImportDialog {

    // === Services ===
    private final WarehouseService warehouseService = new WarehouseService();
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();

    private final ObservableList<OrderDetail> productDetailsList = FXCollections.observableArrayList();

    // === UI Components ===
    public Label unitPriceLabel;
    public Label totalAmountLabel;
    public Label quantityLabel;

    @FXML
    private TextField transactionCodeField;
    @FXML
    private DatePicker createdAtDatePicker;
    @FXML
    private ComboBox<User> createdByComboBox;
    @FXML
    private ComboBox<WarehouseType> transactionTypeComboBox;

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

    @FXML
    private TextField totalAmountField;
    @FXML
    private TextArea noteTextArea;

    @FXML
    private void initialize() {
        initTransactionSection();
        initProductSection();
        initTableView();
        handleTransactionByType();
    }

    // === Transaction Section ===

    private void initTransactionSection() {
        transactionCodeField.setEditable(false);
        transactionTypeComboBox.getItems().addAll(WarehouseType.values());
        transactionTypeComboBox.getSelectionModel().selectFirst();
        transactionTypeComboBox.setOnAction(e -> handleTransactionByType());

        createdByComboBox.getItems().addAll(userService.getWarehouseStaffNames());
        createdByComboBox.getSelectionModel().selectFirst();

        updateTransactionInfo();
    }

    private void updateTransactionInfo() {
        WarehouseType type = transactionTypeComboBox.getValue();
        transactionCodeField.setText(warehouseService.generateTransactionCode(type));
        createdAtDatePicker.setValue(LocalDate.now());
    }

    private void handleTransactionByType() {
        switch (transactionTypeComboBox.getValue()) {
            case NHAP_KHO -> setupImportTransactionUI();
            case XUAT_KHO -> setupExportTransactionUI();
        }
    }

    // === Product Section ===

    private void initProductSection() {
        productComboBox.getItems().addAll(productService.getAllProducts());
        productComboBox.getSelectionModel().selectFirst();
        configureProductComboBoxDisplay();
        TextFieldFormatterUtils.applyBlazingFastCurrencyFormat(unitPriceField);
        TextFieldFormatterUtils.applyBlazingFastCurrencyFormat(totalAmountField);
    }

    private void configureProductComboBoxDisplay() {
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

    // === Table Section ===

    private void initTableView() {
        productTableView.setItems(productDetailsList);

        sttColumn.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(productTableView.getItems().indexOf(col.getValue()) + 1));
        productIdColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getProductId())));
        productNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(productService.getProductById(data.getValue().getProductId()).getName()));
        quantityColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getQuantity()));
        unitPriceColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPrice()));
        totalColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTotal()));

        unitPriceColumn.setCellFactory(TableCellFactoryUtils.currencyCellFactory());
        totalColumn.setCellFactory(TableCellFactoryUtils.currencyCellFactory());

        addRemoveButtonToTable();
    }

    private void addRemoveButtonToTable() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Xóa");

            {
                deleteButton.setOnAction(e -> {
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

    // === UI Switching Based on Transaction Type ===

    private void resetCommonFields() {
        updateTransactionInfo();
        productDetailsList.clear();
        productTableView.refresh();
        quantityField.clear();
        unitPriceField.clear();
        noteTextArea.clear();
        totalAmountField.clear();
    }

    private void setupImportTransactionUI() {
        resetCommonFields();
        setUnitPriceColumnVisible(true);
        totalAmountLabel.setText("Tổng Tiền");
        setTotalAmountFieldReadonly(true);
        setQuantityFieldVisible(true);
        setQuantityColumnVisible(true);
    }

    private void setupExportTransactionUI() {
        resetCommonFields();
        setUnitPriceColumnVisible(false);
        totalAmountLabel.setText("Tổng Tiền");
        setTotalAmountFieldReadonly(true);
        setQuantityFieldVisible(true);
        setQuantityColumnVisible(true);
    }

    private void setQuantityFieldVisible(boolean visible) {
        quantityField.setVisible(visible);
        quantityField.setManaged(visible);
        quantityLabel.setVisible(visible);
        quantityLabel.setManaged(visible);
    }

    private void setQuantityColumnVisible(boolean visible) {
        quantityColumn.setVisible(visible);
        actionColumn.setVisible(visible);
    }

    private void setUnitPriceColumnVisible(boolean visible) {
        unitPriceColumn.setVisible(visible);
        totalColumn.setVisible(visible);
        unitPriceField.setVisible(visible);
        unitPriceField.setManaged(visible);
        unitPriceLabel.setVisible(visible);
        unitPriceLabel.setManaged(visible);
    }

    private void setTotalAmountFieldReadonly(boolean readonly) {
        totalAmountField.setEditable(!readonly);
        totalAmountField.setMouseTransparent(readonly);
        totalAmountField.setFocusTraversable(!readonly);
        totalAmountField.setDisable(false);
    }


    @FXML
    private void onAddProduct() {
        Product product = productComboBox.getValue();
        if (product == null) {
            AlertUtils.showError("Lỗi", "Vui lòng chọn sản phẩm trước khi thêm!");
            return;
        }

        WarehouseType type = transactionTypeComboBox.getValue();
        int quantity = parseQuantity();
        if (quantity == -1) return;

        switch (type) {
            case NHAP_KHO -> {
                BigDecimal price = parseUnitPrice();
                if (price == null) return;

                addOrUpdateProduct(product, quantity, price);
                unitPriceField.clear();
            }

            case XUAT_KHO -> {
                int alreadySelectedQuantity = productDetailsList.stream()
                        .filter(d -> d.getProductId() == product.getId())
                        .mapToInt(OrderDetail::getQuantity)
                        .sum();
                if (quantity + alreadySelectedQuantity > product.getStockQuantity()) {
                    AlertUtils.showError("Lỗi", "Số lượng xuất không thể lớn hơn số lượng tồn kho (" +
                            product.getStockQuantity() + ")!\n" +
                            "Đã chọn: " + alreadySelectedQuantity +
                            ", Có thể thêm tối đa: " + (product.getStockQuantity() - alreadySelectedQuantity));
                    return;
                }

                addOrUpdateProduct(product, quantity, BigDecimal.ZERO);
            }
        }

        // Sau xử lý
        quantityField.clear();
        productTableView.refresh();
        updateTotalAmount();
        productComboBox.requestFocus();
    }


    private int parseQuantity() {
        String text = quantityField.getText().replace(",", "");
        if (text.isEmpty()) {
            AlertUtils.showError("Lỗi", "Vui lòng nhập số lượng sản phẩm!");
            return -1;
        }
        try {
            int quantity = Integer.parseInt(text);
            if (quantity <= 0) {
                AlertUtils.showError("Lỗi", "Số lượng phải lớn hơn 0!");
                return -1;
            }
            return quantity;
        } catch (NumberFormatException e) {
            AlertUtils.showError("Lỗi", "Số lượng không hợp lệ!");
            return -1;
        }
    }

    private BigDecimal parseUnitPrice() {
        String text = unitPriceField.getText();
        if (text.isEmpty()) {
            AlertUtils.showError("Lỗi", "Vui lòng nhập đơn giá sản phẩm!");
            return null;
        }
        BigDecimal price = MoneyUtils.parseCurrencyText(text);
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            AlertUtils.showError("Lỗi", "Đơn giá phải lớn hơn 0!");
            return null;
        }
        return price;
    }

    private void addOrUpdateProduct(Product product, int quantity, BigDecimal price) {
        OrderDetail existing = productDetailsList.stream()
                .filter(d -> d.getProductId() == product.getId())
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            if (price.compareTo(BigDecimal.ZERO) > 0) {
                existing.setPrice(price);
            }
        } else {
            productDetailsList.add(new OrderDetail(0, 0, product.getId(), quantity, price));
        }
    }


    private void updateTotalAmount() {
        if (transactionTypeComboBox.getValue() != WarehouseType.NHAP_KHO) {
            totalAmountField.clear();
            return;
        }

        BigDecimal total = productDetailsList.stream()
                .map(OrderDetail::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAmountField.setText(MoneyUtils.formatVN(total));
    }

    private WarehouseDTO buildTransactionInfo() {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setTransactionCode(transactionCodeField.getText());
        dto.setCreateById(createdByComboBox.getValue().getId());
        dto.setType(transactionTypeComboBox.getValue());
        dto.setCreatedAt(java.time.LocalDateTime.now());
        dto.setNote(noteTextArea.getText());

        return dto;
    }

    private List<WarehouseDTO> buildProductList() {
        WarehouseType type = transactionTypeComboBox.getValue();

        return productDetailsList.stream().map(detail -> {
            WarehouseDTO dto = new WarehouseDTO();
            dto.setProductId(detail.getProductId());
            dto.setQuantity(detail.getQuantity());

            if (type == WarehouseType.NHAP_KHO) {
                dto.setUnitPrice(detail.getPrice());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    private int parseIntOrZero(String text) {
        try {
            return Integer.parseInt(text.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    private void onSave() {
        WarehouseType type = transactionTypeComboBox.getValue();

        try {
            WarehouseDTO transaction = buildTransactionInfo();
            List<WarehouseDTO> productList = buildProductList();

            boolean success = switch (type) {
                case NHAP_KHO -> warehouseService.insertWarehouseImport(transaction, productList);
                case XUAT_KHO -> warehouseService.insertWarehouseExport(transaction, productList);
            };

            if (success) {
                AlertUtils.showInfo("Thành công", "Tạo phiếu " + type.getValue().toLowerCase() + " thành công!");
                resetCommonFields();
            } else {
                AlertUtils.showError("Lỗi", "Lỗi khi tạo phiếu " + type.getValue().toLowerCase());
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Lỗi không xác định: " + e.getMessage());
        }
    }
}