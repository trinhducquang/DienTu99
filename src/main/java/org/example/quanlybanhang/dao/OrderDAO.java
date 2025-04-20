package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.dao.base.CrudDao;
import org.example.quanlybanhang.dto.orderDTO.OrderSummaryDTO;
import org.example.quanlybanhang.enums.ExportStatus;
import org.example.quanlybanhang.enums.OrderStatus;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.model.OrderDetail;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO implements CrudDao<Order> {
    private final Connection connection;

    public OrderDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT * FROM order_summary WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM order_summary";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public boolean save(Order order) {
        throw new UnsupportedOperationException("Use addOrder(order, orderDetails) instead");
    }

    @Override
    public boolean update(Order order) {
        String sql = "UPDATE orders SET status = ?, note = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, order.getStatus().getText());
            stmt.setString(2, order.getNote());
            stmt.setInt(3, order.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void delete(Order order) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addOrder(Order order, List<OrderDetail> orderDetails) {
        String insertOrderSQL = "INSERT INTO orders (employee_id, customer_id, total_price, shipping_fee, order_date, status, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertOrderDetailSQL = "INSERT INTO order_details (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        int orderId = -1;

        try (PreparedStatement orderStmt = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
            orderStmt.setInt(1, order.getEmployeeId());
            orderStmt.setInt(2, order.getCustomerId());
            orderStmt.setBigDecimal(3, order.getTotalPrice());
            orderStmt.setBigDecimal(4, order.getShippingFee());
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
                    detailStmt.setBigDecimal(4, detail.getPrice());
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


    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("order_id"),
                rs.getInt("employee_id"),
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getBigDecimal("total_price"),
                rs.getBigDecimal("shipping_fee"),
                rs.getTimestamp("order_date").toLocalDateTime(),
                OrderStatus.fromString(rs.getString("status")),
                rs.getString("product_names"),
                rs.getString("note")
        );
    }

    private OrderSummaryDTO extractOrderSummaryDTO(ResultSet rs) throws SQLException {
        return new OrderSummaryDTO(
                rs.getInt("order_id"),
                rs.getString("product_ids"),
                rs.getInt("employee_id"),
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getBigDecimal("total_price"),
                rs.getBigDecimal("shipping_fee"),
                rs.getTimestamp("order_date").toLocalDateTime(),
                OrderStatus.fromString(rs.getString("status")),
                rs.getString("product_names"),
                rs.getString("product_images"),
                rs.getString("product_quantities"),
                rs.getString("product_prices"),
                rs.getString("note"),
                ExportStatus.fromValue(rs.getString("export_status"))
        );
    }

    public List<Order> getOrdersByEmployeeId(int employeeId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM order_summary WHERE employee_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public int countCompletedOrders() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, OrderStatus.HOAN_THANH.getText());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public BigDecimal calculateAnnualProfit(int year) {
        String sql = "SELECT " +
                "    SUM(od.price * od.quantity) AS total_revenue, " +
                "    SUM(od.quantity * p.price) AS total_cost, " +  // Sử dụng p.price thay vì p.import_price
                "    SUM(o.shipping_fee) AS total_shipping " +
                "FROM order_details od " +
                "JOIN orders o ON od.order_id = o.id " +
                "JOIN products p ON od.product_id = p.id " +
                "WHERE YEAR(o.order_date) = ? " +
                "AND o.status = ?";

        BigDecimal profit = BigDecimal.ZERO;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Thiết lập các tham số của câu truy vấn
            stmt.setInt(1, year);  // Năm cần tính toán
            stmt.setString(2, OrderStatus.HOAN_THANH.getText());  // Trạng thái "Hoàn thành"

            // Thực thi câu truy vấn
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Lấy kết quả từ ResultSet, đảm bảo không null
                BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                BigDecimal totalCost = rs.getBigDecimal("total_cost");
                BigDecimal totalShipping = rs.getBigDecimal("total_shipping");

                // Nếu giá trị null, gán bằng BigDecimal.ZERO
                totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
                totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
                totalShipping = totalShipping != null ? totalShipping : BigDecimal.ZERO;

                // Tính lợi nhuận
                profit = totalRevenue.subtract(totalCost).subtract(totalShipping);
            }
        } catch (SQLException e) {
            // In lỗi nếu có ngoại lệ
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }

        return profit;
    }

    public List<OrderDetail> getOrderDetailsById(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderDetail detail = new OrderDetail(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("price")
                );
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return details;
    }
}



