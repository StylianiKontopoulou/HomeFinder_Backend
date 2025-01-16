package gr.codingfactory.rest.homefinder.repositories;

import gr.codingfactory.rest.homefinder.models.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;

@RequestScoped
public class UserRepository extends AbstractRepository<User> {

    public UserRepository() {
        super(User.class);
    }

    public List<User> findUsersByCriteria(String name, String email) {
        String query = "SELECT u FROM User u WHERE 1=1";
        if (name != null && !name.isEmpty()) {
            query += " AND (u.firstName LIKE :name OR u.lastName LIKE :name)";
        }
        if (email != null && !email.isEmpty()) {
            query += " AND u.email LIKE :email";
        }
        TypedQuery<User> q = entityManager.createQuery(query, User.class);
        if (name != null && !name.isEmpty()) {
            q.setParameter("name", "%" + name + "%");
        }
        if (email != null && !email.isEmpty()) {
            q.setParameter("email", "%" + email + "%");
        }
        return q.getResultList();
    }

    public User findByUsername(String username) {
        List<User> users = entityManager.createQuery("SELECT u FROM User u WHERE u.userName = :username", User.class)
                .setParameter("username", username)
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }
}