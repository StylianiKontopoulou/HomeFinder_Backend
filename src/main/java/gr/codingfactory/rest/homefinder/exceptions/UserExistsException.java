package gr.codingfactory.rest.homefinder.exceptions;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserExistsException extends RuntimeException {
    public UserExistsException(String message) {
        super(message);
    }
}
