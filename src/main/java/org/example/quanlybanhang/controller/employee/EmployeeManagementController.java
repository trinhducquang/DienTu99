package org.example.quanlybanhang.controller.employee;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import org.example.quanlybanhang.enums.UserRole;
import org.example.quanlybanhang.enums.UserStatus;
import org.example.quanlybanhang.helpers.ButtonTableCell;
import org.example.quanlybanhang.helpers.DialogHelper;
import org.example.quanlybanhang.controller.interfaces.RefreshableView;
import org.example.quanlybanhang.model.Employee;
import org.example.quanlybanhang.security.password.PasswordEncoder;
import org.example.quanlybanhang.service.EmployeeService;
import org.example.quanlybanhang.service.SearchService;
import org.example.quanlybanhang.utils.DatabaseConnection;
import org.example.quanlybanhang.utils.PaginationUtils;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeManagementController implements RefreshableView {

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
    @FXML private TableColumn<Employee, UserStatus> colStatus;
    @FXML private TableColumn<Employee, Void> colOperation;
    @FXML private Pagination pagination;

    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private final ObservableList<Employee> allEmployees = FXCollections.observableArrayList();
    private final Set<Integer> resetEmployeeIds = new HashSet<>();
    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final int itemsPerPage = 18;

    private EmployeeService employeeService;

    public void initialize() {
        employeeService = new EmployeeService(DatabaseConnection.getConnection());

        setupTable();
        setupEditing();
        setupSearch();
        setupButtons();
        setupPagination();
        loadEmployees();
    }

    @Override
    public void refresh() {
        loadEmployees();
        employeeTable.refresh();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colPassword.setCellFactory(column -> new TextFieldTableCell<>(new DefaultStringConverter()) {
            @Override
            public void updateItem(String password, boolean empty) {
                super.updateItem(password, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setEditable(false);
                } else {
                    Employee employee = getTableRow().getItem();
                    if (resetEmployeeIds.contains(employee.getId())) {
                        setText("");
                        setEditable(true);
                    } else {
                        setText("••••••");
                        setEditable(false);
                    }
                }
            }
        });

        colOperation.setCellFactory(col -> new ButtonTableCell<>("Reset Password", this::resetPassword));

        employeeTable.setEditable(true);
        employeeTable.setItems(employeeList);
    }

    private void setupEditing() {
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhone.setCellFactory(TextFieldTableCell.forTableColumn());

        colRole.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(Arrays.stream(UserRole.values())
                        .map(UserRole::getValue).collect(Collectors.toList()))
        ));

        colStatus.setCellFactory(ComboBoxTableCell.forTableColumn(UserStatus.values()));

        colFullName.setOnEditCommit(e -> updateEmployeeField(e.getRowValue(), "fullName", e.getNewValue()));
        colUsername.setOnEditCommit(e -> updateEmployeeField(e.getRowValue(), "username", e.getNewValue()));
        colEmail.setOnEditCommit(e -> updateEmployeeField(e.getRowValue(), "email", e.getNewValue()));
        colPhone.setOnEditCommit(e -> updateEmployeeField(e.getRowValue(), "phone", e.getNewValue()));
        colRole.setOnEditCommit(e -> updateRole(e.getRowValue(), e.getNewValue()));
        colStatus.setOnEditCommit(e -> updateStatus(e.getRowValue(), e.getNewValue()));
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterEmployees(newVal));
    }

    private void setupButtons() {
        addEmployeeButton.setOnAction(e ->
                DialogHelper.showDialog(
                        "/org/example/quanlybanhang/views/employee/employeeManagementDialog.fxml",
                        "Thêm Nhân Viên Mới",
                        (Stage) addEmployeeButton.getScene().getWindow(),
                        this  // Pass the current controller as the refreshable view
                )
        );
    }

    private void loadEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        allEmployees.setAll(employees);
        employeeList.setAll(employees);
    }

    private void filterEmployees(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            employeeList.setAll(allEmployees); // Show all when search is empty
        } else {
            List<Employee> filtered = SearchService.search(
                    allEmployees,
                    keyword,
                    e -> String.valueOf(e.getId()),
                    Employee::getFullName,
                    Employee::getUsername,
                    Employee::getEmail,
                    Employee::getPhone,
                    e -> e.getRole().toString()
            );
            employeeList.setAll(filtered);
        }
        pagination.setCurrentPageIndex(0);
    }

    private void updateEmployeeField(Employee employee, String field, String newValue) {
        employeeService.updateField(employee, field, newValue);
        employeeTable.refresh();
    }

    private void updateRole(Employee employee, String newRole) {
        employeeService.updateRole(employee, newRole);
        employeeTable.refresh();
    }

    private void updateStatus(Employee employee, UserStatus status) {
        if (employee.getRole() == UserRole.ADMIN) {
            employee.setStatus(UserStatus.UNLOCK);
        } else {
            employee.setStatus(status);
            employeeService.updateStatus(employee, status);
        }
        employeeTable.refresh();
    }

    private void resetPassword(Employee employee) {
        resetEmployeeIds.add(employee.getId());
        employee.setPassword("");
        employeeTable.refresh();

        employeeTable.getSelectionModel().select(employee);
        employeeTable.scrollTo(employee);

        colPassword.setOnEditCommit(event -> {
            Employee edited = event.getRowValue();
            if (resetEmployeeIds.contains(edited.getId())) {
                String newPassword = event.getNewValue();
                if (newPassword != null && !newPassword.isEmpty()) {
                    String hashed = new PasswordEncoder().encode(newPassword);
                    edited.setPassword(hashed);
                    employeeService.updateField(edited, "password", newPassword);
                    resetEmployeeIds.remove(edited.getId());
                    employeeTable.refresh();
                }
            }
        });

        int rowIndex = employeeTable.getItems().indexOf(employee);
        employeeTable.edit(rowIndex, colPassword);
    }

    private void setupPagination() {
        PaginationUtils.setup(
                pagination,
                allEmployees,
                employeeList,
                currentPage,
                itemsPerPage,
                (pagedData, pageIndex) -> employeeTable.setItems(employeeList)
        );
    }
}