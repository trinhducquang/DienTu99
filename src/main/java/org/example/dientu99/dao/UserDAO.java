package org.example.dientu99.dao;
import org.example.dientu99.enums.UserRole;
import org.example.dientu99.enums.UserStatus;
import org.example.dientu99.model.User;
import org.example.dientu99.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {
    private final Connection connection;
    public UserDAO(Connection connection) {
        this.connection = connection;
    }

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
                        UserRole.fromString(rs.getString("role")),
                        UserStatus.fromString(rs.getString("status"))

                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


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

    public List<Map<String, Object>> getTopSalesEmployees(int limit) {
        List<Map<String, Object>> topEmployees = new ArrayList<>();

        String sql = "SELECT u.id, u.full_name, " +
                "COUNT(o.id) AS total_orders, " +
                "SUM(o.total_price) AS total_revenue, " +
                "SUM(o.total_price) - SUM(COALESCE(wt.unit_price, 0) * od.quantity) AS total_profit " +
                "FROM users u " +
                "JOIN orders o ON u.id = o.employee_id " +
                "JOIN order_details od ON o.id = od.order_id " +
                "LEFT JOIN (" +
                "    SELECT product_id, AVG(unit_price) as unit_price " +
                "    FROM warehouse_transactions " +
                "    WHERE type = 'Nhập Kho' " +
                "    GROUP BY product_id" +
                ") wt ON od.product_id = wt.product_id " +
                "WHERE u.role = ? AND YEAR(o.order_date) = ? " +  // Sửa tại đây
                "GROUP BY u.id, u.full_name " +
                "ORDER BY total_orders DESC " +
                "LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int currentYear = java.time.Year.now().getValue();

            stmt.setString(1, UserRole.BAN_HANG.getValue());
            stmt.setInt(2, currentYear);
            stmt.setInt(3, limit);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> employeeData = new HashMap<>();
                employeeData.put("id", rs.getInt("id"));
                employeeData.put("fullName", rs.getString("full_name"));
                employeeData.put("totalOrders", rs.getInt("total_orders"));
                employeeData.put("totalRevenue", rs.getBigDecimal("total_revenue"));
                employeeData.put("totalProfit", rs.getBigDecimal("total_profit"));

                topEmployees.add(employeeData);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách nhân viên bán hàng xuất sắc theo năm: " + e.getMessage());
            e.printStackTrace();
        }

        return topEmployees;
    }
}
