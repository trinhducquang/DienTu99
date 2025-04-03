package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection connection;

    public OrderDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM order_summary";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                orders.add(extractOrderFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private Order extractOrderFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int employeeId = resultSet.getInt("employee_id");
        int customerId = resultSet.getInt("customer_id");
        String customerName = resultSet.getString("customer_name");
        double totalPrice = resultSet.getDouble("total_price");
        double shippingFee = resultSet.getDouble("shipping_fee"); // Lấy dữ liệu phí vận chuyển
        LocalDateTime orderDate = resultSet.getTimestamp("order_date").toLocalDateTime();
        OrderStatus status = OrderStatus.fromString(resultSet.getString("status"));
        String productNames = resultSet.getString("product_names");

        return new Order(id, employeeId, customerId, customerName, totalPrice, shippingFee, orderDate, status, productNames);
    }

    public void decreaseProductQuantity(int orderId, int productId) {
        String checkQuantitySQL = "SELECT quantity FROM order_details WHERE order_id = ? AND product_id = ?";
        String updateSQL = "UPDATE order_details SET quantity = quantity - 1 WHERE order_id = ? AND product_id = ?";
        String deleteSQL = "DELETE FROM order_details WHERE order_id = ? AND product_id = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuantitySQL)) {
            checkStmt.setInt(1, orderId);
            checkStmt.setInt(2, productId);
            var resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                int currentQuantity = resultSet.getInt("quantity");
                if (currentQuantity > 1) {
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                        updateStmt.setInt(1, orderId);
                        updateStmt.setInt(2, productId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
                        deleteStmt.setInt(1, orderId);
                        deleteStmt.setInt(2, productId);
                        deleteStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
