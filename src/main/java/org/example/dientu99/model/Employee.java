package org.example.dientu99.model;

import org.example.dientu99.enums.UserRole;
import org.example.dientu99.enums.UserStatus;

public class Employee {
    private int id;
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status;


    public Employee(int id, String fullName, String username, String password, String email, String phone, UserRole role, UserStatus status ) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getFullName() { return fullName; } // Getter cho Tên Nhân Viên
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public UserRole getRole() { return role; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; } // Setter cho Tên Nhân Viên
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(UserRole role) { this.role = role; }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " - " + fullName;
    }

}
