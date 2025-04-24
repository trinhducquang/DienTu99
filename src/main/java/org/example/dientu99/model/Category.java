package org.example.dientu99.model;

public class Category {
    private int id;
    private String name;
    private String description;
    private int parentId;
    private String category;

    public Category(int id, String name, String description, int parentId, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.category = category;
    }

    public Category() {

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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
