package org.example.quanlybanhang.security.auth;

import org.example.quanlybanhang.dao.UserDAO;
import org.example.quanlybanhang.model.User;
import org.example.quanlybanhang.security.password.PasswordEncoder;

public class AuthService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.passwordEncoder = new PasswordEncoder();
    }

    public User login(String username, String rawPassword) {
        User user = userDAO.findByUsername(username);
        if (user != null && passwordEncoder.verify(user.getPassword(), rawPassword)) {
            return user;
        }
        return null;
    }
}
