package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.dao.base.CrudDao;
import org.example.quanlybanhang.enums.UserStatus;
import org.example.quanlybanhang.model.Employee;
import org.example.quanlybanhang.enums.UserRole;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO implements CrudDao<Employee> {
    private final Connection connection;
    private final Dotenv dotenv = Dotenv.load();
    private final String PEPPER = dotenv.get("PEPPER_KEY");

    public EmployeeDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Employee findById(int id) {
        String query = "SELECT id, full_name, username, password, email, phone, role, status FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Employee(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        UserRole.fromString(resultSet.getString("role")),
                        UserStatus.fromString(resultSet.getString("status"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT id, full_name, username, password, email, phone, role, status FROM users";

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
                        UserRole.fromString(resultSet.getString("role")),
                        UserStatus.fromString(resultSet.getString("status"))


                );
                employees.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    @Override
    public boolean save(Employee employee) {
        // Giả định bạn truyền vào rawPassword để xử lý mã hóa phía ngoài (nếu không cần có thể chỉnh lại)
        throw new UnsupportedOperationException("Use save(Employee, rawPassword) instead.");
    }

    public boolean save(Employee employee, String rawPassword) {
        String query = "INSERT INTO users (full_name, username, password, email, phone, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
            statement.setString(7, UserStatus.UNLOCK.getValue());


            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(Employee employee) {
        String query = "UPDATE users SET full_name = ?, username = ?, email = ?, phone = ?, role = ?, status = ?, password = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getUsername());
            statement.setString(3, employee.getEmail());
            statement.setString(4, employee.getPhone());
            statement.setString(5, employee.getRole().getValue());
            statement.setString(6, employee.getStatus().getValue());
            statement.setString(7, employee.getPassword());
            statement.setInt(8, employee.getId());

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public void delete(Employee employee) {
        String query = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, employee.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
