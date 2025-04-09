package org.example.quanlybanhang.service;

import org.example.quanlybanhang.dao.OrderDAO;
import org.example.quanlybanhang.dto.OrderSummaryDTO;
import org.example.quanlybanhang.dto.ProductDisplayInfoDTO;
import org.example.quanlybanhang.model.Order;
import org.example.quanlybanhang.model.OrderDetail;
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

    public Order getOrderById(int id) {
        return orderDAO.findById(id);
    }

    public OrderSummaryDTO getOrderSummaryById(int orderId) {
        return orderDAO.getOrderSummaryById(orderId);
    }

    public int addOrder(Order order, List<OrderDetail> orderDetails) {
        return orderDAO.addOrder(order, orderDetails);
    }

    public boolean updateOrder(Order order) {
        return orderDAO.update(order);
    }

    public void deleteOrder(Order order) {
        orderDAO.delete(order);
    }

    public int getNextOrderId() {
        return orderDAO.getNextOrderId();
    }

    public List<ProductDisplayInfoDTO> getProductDisplayInfoList(int orderId) {
        OrderSummaryDTO summary = orderDAO.getOrderSummaryById(orderId);
        return OrderConverter.toProductDisplayInfoList(summary);
    }
}
