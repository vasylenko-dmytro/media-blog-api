package dao;

import java.util.List;

public interface Dao<E> {
    void create(E entity);
    void update(E entity);
    void delete(E entity);
    List<E> findAll();
    E findById(int id);
}
