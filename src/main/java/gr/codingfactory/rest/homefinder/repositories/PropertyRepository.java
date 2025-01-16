package gr.codingfactory.rest.homefinder.repositories;

import gr.codingfactory.rest.homefinder.models.Property;
import gr.codingfactory.rest.homefinder.models.PropertyType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@RequestScoped
public class PropertyRepository extends AbstractRepository<Property> {

    public PropertyRepository() {
        super(Property.class);
    }

    public List<Property> findPropertiesByCriteria(String location, String type) {
        String query = "SELECT p FROM Property p WHERE 1=1";
        if (location != null && !location.isEmpty()) {
            query += " AND p.area LIKE :location";
        }
        if (type != null && !type.isEmpty()) {
            query += " AND p.propertyType = :type";
        }

        TypedQuery<Property> q = entityManager.createQuery(query, Property.class);
        if (location != null && !location.isEmpty()) {
            q.setParameter("location", "%" + location + "%");
        }
        if (type != null && !type.isEmpty()) {
            q.setParameter("type", PropertyType.valueOf(type));
        }

        return q.getResultList();
    }

    public List<Property> findPropertiesByUserId(Long userId) {
        return entityManager.createQuery("SELECT p FROM Property p WHERE p.user.id = :userId", Property.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}