package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.enums.UserRole;
import org.example.quanlybanhang.model.Employee;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private final Connection connection;
    private final Dotenv dotenv = Dotenv.load();
    private final String PEPPER = dotenv.get("PEPPER_KEY");

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

    public boolean addEmployee(Employee employee, String rawPassword) {
        String query = "INSERT INTO users (full_name, username, password, email, phone, role) VALUES (?, ?, ?, ?, ?, ?)";

        String passwordToHash = rawPassword + PEPPER;
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hashedPassword = argon2.hash(3, 65536, 2, passwordToHash.toCharArray());

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getUsername());
            statement.setString(3, hashedPassword);
            statement.setString(4, employee.getEmail());
            statement.setString(5, employee.getPhone());
            statement.setString(6, UserRole.NHAN_VIEN.getValue());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cập nhật thông tin không bao gồm mật khẩu
    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE users SET full_name = ?, username = ?, email = ?, phone = ?, role = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getUsername());
            statement.setString(3, employee.getEmail());
            statement.setString(4, employee.getPhone());
            statement.setString(5, employee.getRole());
            statement.setInt(6, employee.getId());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
