package org.example.quanlybanhang.controller.employee;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.example.quanlybanhang.dao.EmployeeDAO;
import org.example.quanlybanhang.enums.UserRole;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.model.Employee;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeManagementController {

    @FXML private TextField searchField;
    @FXML private Button addEmployeeButton;
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String> colFullName;
    @FXML private TableColumn<Employee, String> colUsername;
    @FXML private TableColumn<Employee, String> colPassword;
    @FXML private TableColumn<Employee, String> colEmail;
    @FXML private TableColumn<Employee, String> colPhone;
    @FXML private TableColumn<Employee, String> colRole;

    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private final ObservableList<Employee> allEmployees = FXCollections.observableArrayList();
    private EmployeeDAO employeeDAO;

    public void initialize() {
        Connection connection = DatabaseConnection.getConnection();
        employeeDAO = new EmployeeDAO(connection);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Table editable
        employeeTable.setEditable(true);

        // Editable columns (excluding password)
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhone.setCellFactory(TextFieldTableCell.forTableColumn());

        colRole.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(
                        Arrays.stream(UserRole.values())
                                .map(UserRole::getValue)
                                .collect(Collectors.toList())
                )
        ));

        // Hide password text
        colPassword.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String password, boolean empty) {
                super.updateItem(password, empty);
                setText(empty ? null : "••••••");
            }
        });

        // Update handlers
        colFullName.setOnEditCommit(event -> updateEmployee(event.getRowValue(), "fullName", event.getNewValue()));
        colUsername.setOnEditCommit(event -> updateEmployee(event.getRowValue(), "username", event.getNewValue()));
        colEmail.setOnEditCommit(event -> updateEmployee(event.getRowValue(), "email", event.getNewValue()));
        colPhone.setOnEditCommit(event -> updateEmployee(event.getRowValue(), "phone", event.getNewValue()));
        colRole.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            employee.setRole(event.getNewValue());
            employeeDAO.updateEmployee(employee);
        });

        addEmployeeButton.setOnAction(event ->
                DialogHelper.showDialog("/org/example/quanlybanhang/employeeManagementDialog.fxml", "Thêm Nhân Viên Mới"));

        loadEmployees();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterEmployees(newVal));
    }

    private void loadEmployees() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        allEmployees.setAll(employees);
        employeeList.setAll(employees);
        employeeTable.setItems(employeeList);
    }

    private void filterEmployees(String keyword) {
        List<Employee> filtered = SearchService.search(
                allEmployees,
                keyword,
                e -> String.valueOf(e.getId()),
                Employee::getFullName,
                Employee::getUsername,
                Employee::getEmail,
                Employee::getPhone,
                Employee::getRole
        );
        employeeList.setAll(filtered);
    }


    private void updateEmployee(Employee employee, String field, String newValue) {
        switch (field) {
            case "fullName": employee.setFullName(newValue); break;
            case "username": employee.setUsername(newValue); break;
            case "email":    employee.setEmail(newValue); break;
            case "phone":    employee.setPhone(newValue); break;
        }

        employeeDAO.updateEmployee(employee);
        employeeTable.refresh();
    }
}
