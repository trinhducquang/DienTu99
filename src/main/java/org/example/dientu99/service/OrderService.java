package org.example.dientu99.service;

import org.example.dientu99.dao.OrderDAO;
import org.example.dientu99.dto.orderDTO.OrderSummaryDTO;
import org.example.dientu99.dto.productDTO.ProductDisplayInfoDTO;
import org.example.dientu99.enums.ExportStatus;
import org.example.dientu99.model.Order;
import org.example.dientu99.model.OrderDetail;
import org.example.dientu99.utils.OrderConverter;

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

    public boolean updateOrderStatus(Order order) {
        return orderDAO.update(order);
    }

    public List<OrderDetail> getOrderDetailsById(int orderId) {
        return orderDAO.getOrderDetailsById(orderId);
    }

    public Order getOrderById(int orderId) {
        return orderDAO.findById(orderId);
    }

    public boolean updateOrderExportStatus(Integer orderId, ExportStatus status) {
        return orderDAO.updateOrderExportStatus(orderId, status);
    }
}