package org.example.dientu99.security.auth;

import org.example.dientu99.dao.UserDAO;
import org.example.dientu99.model.User;
import org.example.dientu99.security.password.PasswordEncoder;

import org.example.dientu99.enums.UserStatus;

public class AuthService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.passwordEncoder = new PasswordEncoder();
    }

    public User login(String username, String rawPassword) {
        User user = userDAO.findByUsername(username);
        if (user == null) return null;

        if (user.getStatus() == UserStatus.LOCK) {
            System.out.println("Tài khoản bị khóa: " + username);
            return null;
        }

        if (passwordEncoder.verify(user.getPassword(), rawPassword)) {
            return user;
        }

        return null;
    }

}
