package org.example.quanlybanhang.dao.base;

import java.util.List;


public interface ReadOnlyDao<T> {
    T findById(int id);
    List<T> getAll();
}
