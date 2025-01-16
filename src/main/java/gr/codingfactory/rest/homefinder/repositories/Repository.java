package gr.codingfactory.rest.homefinder.repositories;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.PersistenceException;

public interface Repository<T> {

    T create(T entity);
    void update(T entity) throws PersistenceException;
    void delete(T entity);
    void softDelete(Long id);
    void restore(Long id);
    void deleteById(Long id);
    Optional<T> findById(Long id);
    List<T> findAll();
}