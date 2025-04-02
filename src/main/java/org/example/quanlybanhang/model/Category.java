package org.example.quanlybanhang.model;

public class Category {
    private int id;
    private String name;
    private String description;
    private int parentId; // Thêm parentId

    public Category(int id, String name, String description, int parentId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getParentId() {
        return parentId;
    }
}
