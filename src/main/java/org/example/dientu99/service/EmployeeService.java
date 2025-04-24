package org.example.dientu99.service;

import org.example.dientu99.dao.EmployeeDAO;
import org.example.dientu99.enums.UserRole;
import org.example.dientu99.enums.UserStatus;
import org.example.dientu99.model.Employee;
import org.example.dientu99.security.password.PasswordEncoder;

import java.sql.Connection;
import java.util.List;

public class EmployeeService {
    private final EmployeeDAO employeeDAO;
    private final PasswordEncoder passwordEncoder = new PasswordEncoder();


    public EmployeeService(Connection connection) {
        this.employeeDAO = new EmployeeDAO(connection);
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAll();
    }

    public void updateField(Employee employee, String field, String newValue) {
        if (field.equals("password")) {
            String hashedPassword = passwordEncoder.encode(newValue);
            employee.setPassword(hashedPassword);
        } else {
            switch (field) {
                case "fullName": employee.setFullName(newValue); break;
                case "username": employee.setUsername(newValue); break;
                case "email":    employee.setEmail(newValue); break;
                case "phone":    employee.setPhone(newValue); break;
            }
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
        employee.setRole(UserRole.fromString(newRole));
        employeeDAO.update(employee);
    }

    public void updateStatus(Employee employee, UserStatus newStatus) {
        employee.setStatus(newStatus);  // Cập nhật trạng thái trong đối tượng employee
        employeeDAO.update(employee);   // Cập nhật thông tin vào cơ sở dữ liệu
    }

}
