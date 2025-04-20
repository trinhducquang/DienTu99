package org.example.quanlybanhang.model;

import org.example.quanlybanhang.enums.ExportStatus;
import org.example.quanlybanhang.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Order {
    private int id;
    private int employeeId;
    private int customerId;
    private String customerName;
    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private String productNames;
    private String note;
    private ExportStatus exportStatus;

    // Constructor
    public Order(int id, int employeeId, int customerId, String customerName, BigDecimal totalPrice, BigDecimal shippingFee,
                 LocalDateTime orderDate, OrderStatus status, String productNames, String note) {
        this.id = id;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.shippingFee = shippingFee;
        this.orderDate = orderDate;
        this.status = status;
        this.productNames = productNames;
        this.note = note;
    }

    public Order() {

    }

    public Order(int id, int employeeId, int customerId, String customerName, BigDecimal totalPrice, BigDecimal shippingFee,
                 LocalDateTime orderDate, OrderStatus status, String productNames, String note, ExportStatus exportStatus) {
        this.id = id;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.shippingFee = shippingFee;
        this.orderDate = orderDate;
        this.status = status;
        this.productNames = productNames;
        this.note = note;
        this.exportStatus = exportStatus;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ExportStatus getExportStatus() {
        return exportStatus;
    }

    public LocalDate getOrderDateAsLocalDate() {
        if (orderDate == null) {
            return null;
        }
        return orderDate.toLocalDate();
    }

    public String getFormattedOrderDate() {
        if (orderDate == null) {
            return "";
        }
        return orderDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}