package org.example.quanlybanhang.dao.base;

public interface CrudDao<T> extends ReadOnlyDao<T> {
    void save(T t);
    void update(T t);
    void delete(T t);
}