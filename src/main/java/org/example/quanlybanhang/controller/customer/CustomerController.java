package org.example.quanlybanhang.controller.customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Customer;
import org.example.quanlybanhang.service.CustomerService;
import org.example.quanlybanhang.service.SearchService;

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

    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private final CustomerService customerService = new CustomerService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        customerTable.setEditable(true);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        nameColumn.setOnEditCommit(event -> updateCustomer(event.getRowValue(), "name", event.getNewValue()));
        phoneColumn.setOnEditCommit(event -> updateCustomer(event.getRowValue(), "phone", event.getNewValue()));
        emailColumn.setOnEditCommit(event -> updateCustomer(event.getRowValue(), "email", event.getNewValue()));
        addressColumn.setOnEditCommit(event -> updateCustomer(event.getRowValue(), "address", event.getNewValue()));

        loadCustomerData();

        addCustomerButton.setOnAction(event -> {
            DialogHelper.showDialog("/org/example/quanlybanhang/CustomerDialog.fxml", "Thêm Khách Hàng Mới",  (Stage) addCustomerButton.getScene().getWindow());
            loadCustomerData();
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchCustomers(newVal));
    }

    private void loadCustomerData() {
        allCustomers.setAll(customerService.getAllCustomers());
        customerTable.setItems(allCustomers);
    }

    private void searchCustomers(String keyword) {
        List<Customer> filteredList = SearchService.search(allCustomers, keyword, Customer::getName, Customer::getPhone);
        customerTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    private void updateCustomer(Customer customer, String field, String newValue) {
        switch (field) {
            case "name" -> customer.setName(newValue);
            case "phone" -> customer.setPhone(newValue);
            case "email" -> customer.setEmail(newValue);
            case "address" -> customer.setAddress(newValue);
        }
        customerService.updateCustomer(customer);
    }
}
