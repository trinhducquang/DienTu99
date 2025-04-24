package org.example.dientu99.service;

import org.example.dientu99.dao.CustomerDAO;
import org.example.dientu99.model.Customer;

import java.util.List;

public class CustomerService {
    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> getAllCustomers() {
        return customerDAO.getAll();
    }

    public void updateCustomer(Customer customer) {
        customerDAO.update(customer);
    }

    public boolean addCustomer(Customer customer) {
        return customerDAO.save(customer);
    }
}
