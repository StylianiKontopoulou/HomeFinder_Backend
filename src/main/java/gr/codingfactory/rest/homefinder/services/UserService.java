package gr.codingfactory.rest.homefinder.services;

import gr.codingfactory.rest.homefinder.models.User;
import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    List<User> searchUsers(String name, String email);

    User getUserById(Long userId);
    User getByUsername(String username);
    User registerUser(User user);

    User authenticate(String username, String password);

    void updateUser(Long userId, User user);

    void deleteUser(Long userId);
}