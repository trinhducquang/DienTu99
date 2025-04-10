package org.example.quanlybanhang.service;

import org.example.quanlybanhang.dao.UserDAO;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.List;



public class UserService {
    private UserDAO userDAO;

    public UserService() {
        Connection connection = DatabaseConnection.getConnection();
        this.userDAO = new UserDAO(connection);
    }

    public List<User> getWarehouseStaffNames() {
        return userDAO.getWarehouseStaffNames();
    }


}
