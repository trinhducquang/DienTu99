package org.example.quanlybanhang.dao.base;

public interface CrudDao<T> extends ReadOnlyDao<T> {
    boolean save(T t);
    boolean update(T t);
    void delete(T t);
}