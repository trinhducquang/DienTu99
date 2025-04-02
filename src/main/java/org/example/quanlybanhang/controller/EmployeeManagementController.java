package org.example.quanlybanhang.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.example.quanlybanhang.dao.EmployeeDAO;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Employee;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeManagementController {
    @FXML
    private TextField searchField;
    @FXML
    private Button addEmployeeButton;
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Integer> colId;
    @FXML
    private TableColumn<Employee, String> colFullName;
    @FXML
    private TableColumn<Employee, String> colUsername;
    @FXML
    private TableColumn<Employee, String> colPassword;
    @FXML
    private TableColumn<Employee, String> colEmail;
    @FXML
    private TableColumn<Employee, String> colPhone;

    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private ObservableList<Employee> allEmployees = FXCollections.observableArrayList();
    private EmployeeDAO employeeDAO;

    public void initialize() {
        Connection connection = DatabaseConnection.getConnection();
        employeeDAO = new EmployeeDAO(connection);

        // Thiết lập các cột
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Cho phép chỉnh sửa
        employeeTable.setEditable(true);

        // Dùng TextFieldTableCell để chỉnh sửa dữ liệu
        colFullName.setCellFactory(TextFieldTableCell.forTableColumn());
        colUsername.setCellFactory(TextFieldTableCell.forTableColumn());
        colPassword.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhone.setCellFactory(TextFieldTableCell.forTableColumn());

        // Bắt sự kiện cập nhật dữ liệu sau khi chỉnh sửa
        colFullName.setOnEditCommit(event -> updateEmployee(event.getRowValue(), "fullName", event.getNewValue()));
        colUsername.setOnEditCommit(event -> updateEmployee(event.getRowValue(), "username", event.getNewValue()));
        colPassword.setOnEditCommit(event -> updateEmployee(event.getRowValue(), "password", event.getNewValue()));
        colEmail.setOnEditCommit(event -> updateEmployee(event.getRowValue(), "email", event.getNewValue()));
        colPhone.setOnEditCommit(event -> updateEmployee(event.getRowValue(), "phone", event.getNewValue()));

        addEmployeeButton.setOnAction(event -> DialogHelper.showDialog("/org/example/quanlybanhang/employeeManagementDialog.fxml", "Thêm Nhân Viên bán Mới"));


        loadEmployees();

        // Lọc dữ liệu khi nhập vào ô tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterEmployees(newValue));
    }

    private void loadEmployees() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        allEmployees.setAll(employees);
        employeeList.setAll(employees);
        employeeTable.setItems(employeeList);
    }

    private void filterEmployees(String keyword) {
        List<Employee> filteredEmployees = allEmployees.stream()
                .filter(employee -> employee.getFullName().toLowerCase().contains(keyword.toLowerCase()) ||
                        employee.getUsername().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        employeeList.setAll(filteredEmployees);
    }

    private void updateEmployee(Employee employee, String field, String newValue) {
        switch (field) {
            case "fullName":
                employee.setFullName(newValue);
                break;
            case "username":
                employee.setUsername(newValue);
                break;
            case "password":
                employee.setPassword(newValue);
                break;
            case "email":
                employee.setEmail(newValue);
                break;
            case "phone":
                employee.setPhone(newValue);
                break;
        }
        employeeDAO.updateEmployee(employee);
    }
}
