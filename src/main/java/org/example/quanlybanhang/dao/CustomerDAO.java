package org.example.quanlybanhang.dao;

import org.example.quanlybanhang.utils.DatabaseConnection;
import org.example.quanlybanhang.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT id, name, phone, email, address FROM customers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static boolean addCustomer(Customer customer) {
        String query = "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getAddress());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateCustomer(Customer customer) {
        String query = "UPDATE customers SET name = ?, phone = ?, email = ?, address = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getAddress());
            pstmt.setInt(5, customer.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
