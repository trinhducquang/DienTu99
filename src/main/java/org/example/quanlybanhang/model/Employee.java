package org.example.quanlybanhang.model;

import org.example.quanlybanhang.enums.UserRole;

public class Employee {
    private int id;
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String phone;
    private UserRole role;


    public Employee(int id, String fullName, String username, String password, String email, String phone, UserRole role ) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
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

    @Override
    public String toString() {
        return id + " - " + fullName;
    }

}
