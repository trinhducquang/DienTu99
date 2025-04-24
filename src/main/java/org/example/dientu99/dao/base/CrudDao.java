package org.example.dientu99.dao.base;

public interface CrudDao<T> extends ReadOnlyDao<T> {
    boolean save(T t);
    boolean update(T t);
}