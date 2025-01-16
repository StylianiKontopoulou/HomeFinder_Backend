package gr.codingfactory.rest.homefinder.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T> implements Repository<T> {

    @PersistenceContext(unitName = "Persistence")
    protected EntityManager entityManager;

    private Class<T> entityClass;

    public AbstractRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    @Transactional
    public T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public void update(T entity) throws PersistenceException {
        entityManager.merge(entity);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        entityManager.remove(entity);
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return entityManager.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.isActive = true", entityClass).getResultList();
    }

    @Transactional
    @Override
    public void softDelete(Long id) {
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.createQuery("UPDATE " + entityClass.getSimpleName() + " e SET e.isActive = false WHERE e.id = :id")
                         .setParameter("id", id)
                         .executeUpdate();
        }
    }

    @Transactional
    @Override
    public void restore(Long id) {
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.createQuery("UPDATE " + entityClass.getSimpleName() + " e SET e.isActive = true WHERE e.id = :id")
                         .setParameter("id", id)
                         .executeUpdate();
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
