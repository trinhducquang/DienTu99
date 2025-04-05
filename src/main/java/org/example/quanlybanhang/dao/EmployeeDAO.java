package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private Connection connection;

    public EmployeeDAO(Connection connection) {
        this.connection = connection;
    }
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT id, full_name, username, password, email, phone, role FROM users";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("role")
                );
                employees.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    // Thêm nhân viên mới với role mặc định là "nhanvien"
    public boolean addEmployee(Employee employee) {
        String query = "INSERT INTO users (full_name, username, password, email, phone, role) VALUES (?, ?, ?, ?, ?, 'nhanvien')";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getUsername());
            statement.setString(3, employee.getPassword());
            statement.setString(4, employee.getEmail());
            statement.setString(5, employee.getPhone());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cập nhật thông tin nhân viên
    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE users SET full_name = ?, username = ?, password = ?, email = ?, phone = ?, role = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getUsername());
            statement.setString(3, employee.getPassword());
            statement.setString(4, employee.getEmail());
            statement.setString(5, employee.getPhone());
            statement.setString(6, employee.getRole());
            statement.setInt(7, employee.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
