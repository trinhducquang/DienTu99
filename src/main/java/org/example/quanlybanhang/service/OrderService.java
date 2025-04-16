package org.example.quanlybanhang.service;

import org.example.quanlybanhang.dao.OrderDAO;
import org.example.quanlybanhang.dto.orderDTO.OrderSummaryDTO;
import org.example.quanlybanhang.dto.productDTO.ProductDisplayInfoDTO;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.utils.OrderConverter;

import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
    }

    public List<Order> getAllOrders() {
        return orderDAO.getAll();
    }

    public OrderSummaryDTO getOrderSummaryById(int orderId) {
        return orderDAO.getOrderSummaryById(orderId);
    }

    public List<Order> getOrdersByEmployeeId(int employeeId) {
        return orderDAO.getOrdersByEmployeeId(employeeId);
    }

    public List<ProductDisplayInfoDTO> getProductDisplayInfoList(int orderId) {
        OrderSummaryDTO summary = orderDAO.getOrderSummaryById(orderId);
        return OrderConverter.toProductDisplayInfoList(summary);
    }
}
