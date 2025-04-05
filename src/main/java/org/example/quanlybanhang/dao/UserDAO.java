package org.example.quanlybanhang.dao;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.quanlybanhang.enums.UserRole;
import org.example.quanlybanhang.model.User;

import java.sql.*;

public class UserDAO {
    private final Connection connection;
    private final Dotenv dotenv = Dotenv.load();
    private final String PEPPER = dotenv.get("PEPPER_KEY");

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User dangNhap(String username, String rawPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                // Gộp password người dùng nhập + PEPPER
                String inputPasswordWithPepper = rawPassword + PEPPER;

                Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

                // So sánh bằng verify (nó sẽ hash lại password người dùng nhập và so với hash đã lưu)
                if (argon2.verify(hashedPassword, inputPasswordWithPepper.toCharArray())) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            hashedPassword,
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            UserRole.fromString(rs.getString("role"))
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Đăng nhập thất bại
    }
}
