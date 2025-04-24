package org.example.dientu99.service;

import org.example.dientu99.dao.UserDAO;
import org.example.dientu99.model.User;
import org.example.dientu99.utils.DatabaseConnection;

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
