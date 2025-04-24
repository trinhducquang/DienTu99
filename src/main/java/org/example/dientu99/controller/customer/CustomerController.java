package org.example.dientu99.controller.customer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.example.dientu99.helpers.DialogHelper;
import org.example.dientu99.model.Customer;
import org.example.dientu99.service.CustomerService;
import org.example.dientu99.service.SearchService;
import org.example.dientu99.utils.PaginationUtils;

import java.util.List;

public class CustomerController {

    @FXML private Button addCustomerButton;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private final CustomerService customerService = new CustomerService();

    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final int itemsPerPage = 18;

    @FXML
    public void initialize() {
        setupTableColumns();
        setEditableColumns();
        setupAddCustomerButton();
        setupSearchAndFilter();

        loadAllCustomers();
        setupPagination();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    private void setEditableColumns() {
        customerTable.setEditable(true);

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        nameColumn.setOnEditCommit(event -> updateCustomer(event.getRowValue(), "name", event.getNewValue()));
        phoneColumn.setOnEditCommit(event -> updateCustomer(event.getRowValue(), "phone", event.getNewValue()));
        emailColumn.setOnEditCommit(event -> updateCustomer(event.getRowValue(), "email", event.getNewValue()));
        addressColumn.setOnEditCommit(event -> updateCustomer(event.getRowValue(), "address", event.getNewValue()));
    }

    private void setupAddCustomerButton() {
        addCustomerButton.setOnAction(event -> {
            DialogHelper.showDialog(
                    "/org/example/dientu99/views/customer/CustomerDialog.fxml",
                    "Thêm Khách Hàng Mới",
                    (Stage) addCustomerButton.getScene().getWindow()
            );
            loadAllCustomers(); // Reload sau khi thêm
        });
    }

    private void setupSearchAndFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterBySearch(newVal));
    }

    private void filterBySearch(String keyword) {
        List<Customer> filtered = SearchService.search(
                allCustomers,
                keyword,
                Customer::getName,
                Customer::getPhone,
                Customer::getEmail,
                Customer::getAddress
        );
        customerList.setAll(filtered);
    }

    private void updateCustomer(Customer customer, String field, String newValue) {
        switch (field) {
            case "name" -> customer.setName(newValue);
            case "phone" -> customer.setPhone(newValue);
            case "email" -> customer.setEmail(newValue);
            case "address" -> customer.setAddress(newValue);
        }
        customerService.updateCustomer(customer);
        loadAllCustomers(); // Tải lại toàn bộ sau khi cập nhật
    }

    private void loadAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        allCustomers.setAll(customers);
        customerList.setAll(customers);
    }

    private void setupPagination() {
        PaginationUtils.setup(
                pagination,
                allCustomers,
                customerList,
                currentPage,
                itemsPerPage,
                (pagedData, pageIndex) -> customerTable.setItems(customerList)
        );
    }
}
