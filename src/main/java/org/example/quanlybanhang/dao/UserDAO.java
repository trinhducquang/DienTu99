package org.example.quanlybanhang.dao;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.quanlybanhang.enums.UserRole;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;
    private final Dotenv dotenv = Dotenv.load();
    private final String PEPPER = dotenv.get("PEPPER_KEY");

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // Phương thức tìm người dùng theo username
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        UserRole.fromString(rs.getString("role"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Phương thức lấy danh sách nhân viên kho
    public List<User> getWarehouseStaffNames() {
        List<User> warehouseStaff = new ArrayList<>();
        String query = "SELECT id, full_name FROM users WHERE role = 'Nhân viên kho'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn != null ? conn.createStatement() : null;
             ResultSet rs = stmt != null ? stmt.executeQuery(query) : null) {

            while (rs != null && rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                warehouseStaff.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return warehouseStaff;
    }

    // Phương thức thêm người dùng mới
    public void addUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Mã hóa mật khẩu cần thực hiện ở nơi khác
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getRole().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Phương thức cập nhật thông tin người dùng
    public void updateUser(User user) {
        String sql = "UPDATE users SET username=?, password=?, full_name=?, email=?, phone=?, role=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Mã hóa mật khẩu cần thực hiện ở nơi khác
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getRole().toString());
            stmt.setInt(7, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Phương thức xóa người dùng
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Phương thức lấy danh sách tất cả người dùng
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        UserRole.fromString(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Phương thức đăng nhập
    public User login(String username, String rawPassword) {
        String sql = "SELECT * FROM users WHERE username=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                // Kiểm tra mật khẩu (mã hóa mật khẩu cần thực hiện ở nơi khác)
                if (rawPassword.equals(hashedPassword)) { // Thay đổi logic kiểm tra mật khẩu nếu cần
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            UserRole.fromString(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

}
