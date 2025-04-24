package org.example.dientu99.dao.base;

import java.util.List;


public interface ReadOnlyDao<T> {
    T findById(int id);
    List<T> getAll();
}
