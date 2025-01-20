package gr.codingfactory.rest.homefinder.services;

import gr.codingfactory.rest.homefinder.exceptions.InvalidInputException;
import gr.codingfactory.rest.homefinder.exceptions.MissingInputException;
import gr.codingfactory.rest.homefinder.exceptions.UserExistsException;
import gr.codingfactory.rest.homefinder.exceptions.UserNotFoundException;
import gr.codingfactory.rest.homefinder.models.User;
import gr.codingfactory.rest.homefinder.models.UserTypeEnum;
import gr.codingfactory.rest.homefinder.repositories.UserRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class UserServiceImpl implements UserService {

    @Inject
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> searchUsers(String name, String email) {
        return userRepository.findUsersByCriteria(name, email);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public User registerUser(User user) {
        if (!PatternService.EMAIL_PATTERN.matcher(user.getEmail().trim()).matches()) {
            throw new InvalidInputException("This is not a valid email");
        }
        if (!PatternService.PASSWORD_PATTERN.matcher(user.getPassword().trim()).matches()) {
            throw new InvalidInputException("This is not a valid password");
        }
//        if (!PatternService.VAT_PATTERN.matcher(user.getVat().trim()).matches()) {
//            throw new InvalidInputException("This is not a valid vat number");
//        }
        if (!PatternService.PHONE_NUMBER_PATTERN.matcher(user.getPhoneNumber().trim()).matches()) {
            throw new InvalidInputException("This is not a valid phone number");
        }

        try {
            user.setIsActive(Boolean.TRUE);
            user.setUserType(UserTypeEnum.PROPERTY_OWNER);
            return userRepository.create(user);
        } catch (PersistenceException e) {
            log.error(e.getMessage());
            // a null value was passed
            if (e.getMessage().contains("PropertyValueException")) {
                throw new MissingInputException("Required input missing");
                // the user already exists
            } else {
                throw new UserExistsException("This user already exists");
            }
        }
    }

    @Override
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password) || !user.getIsActive()) {
            throw new UserNotFoundException("User not found");
        }

        return user;
    }

    @Override
    public void updateUser(Long userId, User user) {
        if (!PatternService.EMAIL_PATTERN.matcher(user.getEmail().trim()).matches()) {
            throw new InvalidInputException("This is not a valid email");
        }
        if (!PatternService.PASSWORD_PATTERN.matcher(user.getPassword().trim()).matches()) {
            throw new InvalidInputException("This is not a valid password");
        }

        User existingUser = userRepository.findById(userId).orElse(null);
        if (existingUser != null) {
            // checks if the id of the user belongs to an existing user. Throws an exception otherwise
            // only three fields can be updated
//            if (!existingUser.getFirstName().equals(user.getFirstName())
////                    || !existingUser.getLastName().equals(user.getLastName())
////                    || !existingUser.getPhoneNumber().equals(user.getPhoneNumber())
//////                    || !existingUser.getVat().equals(user.getVat())
////                    || !existingUser.getUserName().equals(user.getUserName())
//                    || !Objects.equals(existingUser.getIsActive(), user.getIsActive())) {
//
//                throw new InvalidInputException("You tried to update an unmodifiable field. You can only update the email, password and address");
//            }
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setUserName(user.getUserName());
            existingUser.setPhoneNumber(user.getPhoneNumber());
//            existingUser.setAddress(user.getAddress());

            try {
                userRepository.update(existingUser);
            } catch (PersistenceException e) {
                log.error(e.getMessage());
                // a null value was passed
                if (e.getMessage().contains("PropertyValueException")) {
                    throw new MissingInputException("Required input missing");
                    // the user already exists
                } else {
                    throw new UserExistsException("This user already exists");
                }
            }
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.softDelete(userId);
    }
}
