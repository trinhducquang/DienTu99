package org.example.quanlybanhang.service;

import org.example.quanlybanhang.dao.EmployeeDAO;
import org.example.quanlybanhang.model.Employee;

import java.sql.Connection;
import java.util.List;

public class EmployeeService {
    private final EmployeeDAO employeeDAO;

    public EmployeeService(Connection connection) {
        this.employeeDAO = new EmployeeDAO(connection);
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAll();
    }


    public void updateField(Employee employee, String field, String newValue) {
        switch (field) {
            case "fullName": employee.setFullName(newValue); break;
            case "username": employee.setUsername(newValue); break;
            case "email":    employee.setEmail(newValue); break;
            case "phone":    employee.setPhone(newValue); break;
        }
        employeeDAO.update(employee);
    }

    public boolean addEmployee(Employee employee, String retypePassword) {
        if (!employee.getPassword().equals(retypePassword)) {
            System.out.println("❌ Mật khẩu không khớp!");
            return false;
        }

        return employeeDAO.save(employee, employee.getPassword());
    }


    public void updateRole(Employee employee, String newRole) {
        employee.setRole(newRole);
        employeeDAO.update(employee);
    }
}
