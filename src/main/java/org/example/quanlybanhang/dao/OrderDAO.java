package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.dto.OrderSummaryDTO;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.model.OrderDetail;
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
        int id = resultSet.getInt("order_id");
        int employeeId = resultSet.getInt("employee_id");
        int customerId = resultSet.getInt("customer_id");
        String customerName = resultSet.getString("customer_name");
        double totalPrice = resultSet.getDouble("total_price");
        double shippingFee = resultSet.getDouble("shipping_fee");
        LocalDateTime orderDate = resultSet.getTimestamp("order_date").toLocalDateTime();
        OrderStatus status = OrderStatus.fromString(resultSet.getString("status"));
        String productNames = resultSet.getString("product_names");
        String note = resultSet.getString("note");

        return new Order(id, employeeId, customerId, customerName, totalPrice, shippingFee, orderDate, status, productNames, note);
    }


    public int addOrder(Order order, List<OrderDetail> orderDetails) {
        String insertOrderSQL = "INSERT INTO orders (employee_id, customer_id, total_price, shipping_fee, order_date, status, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertOrderDetailSQL = "INSERT INTO order_details (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        int orderId = -1;

        try (PreparedStatement orderStmt = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
            orderStmt.setInt(1, order.getEmployeeId());
            orderStmt.setInt(2, order.getCustomerId());
            orderStmt.setDouble(3, order.getTotalPrice());
            orderStmt.setDouble(4, order.getShippingFee());
            orderStmt.setTimestamp(5, Timestamp.valueOf(order.getOrderDate()));
            orderStmt.setString(6, order.getStatus().getText());
            orderStmt.setString(7, order.getNote());

            int affectedRows = orderStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            try (PreparedStatement detailStmt = connection.prepareStatement(insertOrderDetailSQL)) {
                for (OrderDetail detail : orderDetails) {
                    detailStmt.setInt(1, orderId);
                    detailStmt.setInt(2, detail.getProductId());
                    detailStmt.setInt(3, detail.getQuantity());
                    detailStmt.setDouble(4, detail.getPrice());
                    detailStmt.addBatch();
                }
                detailStmt.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderId;
    }

    public int getNextOrderId() {
        String sql = "SELECT MAX(id) FROM orders";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public OrderSummaryDTO getOrderSummaryById(int orderId) {
        String sql = "SELECT * FROM order_summary WHERE order_id  = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extractOrderSummaryDTO(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OrderSummaryDTO extractOrderSummaryDTO(ResultSet rs) throws SQLException {
        return new OrderSummaryDTO(
                rs.getInt("order_id"),
                rs.getString("product_ids"),
                rs.getInt("employee_id"),
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getDouble("total_price"),
                rs.getDouble("shipping_fee"),
                rs.getTimestamp("order_date").toLocalDateTime(),
                OrderStatus.fromString(rs.getString("status")),
                rs.getString("product_names"),
                rs.getString("product_images"),
                rs.getString("product_quantities"),
                rs.getString("product_prices"),
                rs.getString("note")
        );
    }


}
