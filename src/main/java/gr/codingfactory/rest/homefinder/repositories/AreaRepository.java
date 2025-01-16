package gr.codingfactory.rest.homefinder.repositories;

import gr.codingfactory.rest.homefinder.models.Area;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@RequestScoped
public class AreaRepository {
    
    @PersistenceContext(unitName = "Persistence")
    protected EntityManager entityManager;

    public AreaRepository() {
    }

    public List<Area> findAll() {
        return entityManager.createQuery("SELECT a FROM Area a", Area.class).getResultList();
    }
}