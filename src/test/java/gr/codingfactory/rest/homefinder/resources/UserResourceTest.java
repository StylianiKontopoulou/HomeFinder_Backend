package gr.codingfactory.rest.homefinder.resources;

import gr.codingfactory.rest.homefinder.resources.UserResource;
import gr.codingfactory.rest.homefinder.models.LoginRequest;
import gr.codingfactory.rest.homefinder.models.User;
import gr.codingfactory.rest.homefinder.models.UserTypeEnum;
import gr.codingfactory.rest.homefinder.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserResource userResource;

    @Mock
    private HttpServletRequest requestContext;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User(1L, "FirstName", "LastName", "1234567890", "mail@mail.mail", "username", "password", Boolean.TRUE, UserTypeEnum.PROPERTY_OWNER, null);
    }

    // Helper method to mock current user context (for methods that need authentication)
    private void mockCurrentUser(User currentUser) {
        when(requestContext.getAttribute("currentUser")).thenReturn(currentUser);
    }

    @Test
    public void testRegisterUser() {
        // Arrange
        when(userService.registerUser(any(User.class))).thenReturn(testUser);

        // Act
        Response response = userResource.registerUser(testUser);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(testUser, response.getEntity());
    }

    @Test
    public void testRegisterUserThrowsException() {
        // Arrange
        when(userService.registerUser(any(User.class))).thenThrow(new RuntimeException("Error registering user"));

        // Act
        Response response = userResource.registerUser(testUser);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Error registering user", response.getEntity());
    }

    @Test
    public void testLoginUser() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        when(userService.authenticate("testUser", "password")).thenReturn(testUser);

        // Act
        Response response = userResource.loginUser(loginRequest);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(testUser, response.getEntity());
    }

    @Test
    public void testLoginUserThrowsException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        when(userService.authenticate("testUser", "password")).thenThrow(new RuntimeException("Error logging in"));

        // Act
        Response response = userResource.loginUser(loginRequest);

        // Assert
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals("Error logging in", response.getEntity());
    }

    @Test
    public void testGetUserDetails() {
        // Arrange
        Long userId = 1L;
        mockCurrentUser(testUser);
        when(userService.getUserById(userId)).thenReturn(testUser);

        // Act
        Response response = userResource.getUserDetails(userId);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(testUser, response.getEntity());
    }

    @Test
    public void testGetUserDetailsForbidden() {
        // Arrange
        User currentUser = new User(2L, "FirstName2", "LastName2", "1234567890", "mail2@mail.mail", "username2", "password", Boolean.TRUE, UserTypeEnum.PROPERTY_OWNER, null);
        mockCurrentUser(currentUser);
        Long userId = 1L;

        // Act
        Response response = userResource.getUserDetails(userId);

        // Assert
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertEquals("You are not allowed to access this resource", response.getEntity());
    }

    @Test
    public void testUpdateUserDetails() {
        // Arrange
        Long userId = 1L;
        mockCurrentUser(testUser);
        User updatedUser = new User(userId, "FirstName2", "LastName2", "1234567890", "mail2@mail.mail", "username2", "password", Boolean.TRUE, UserTypeEnum.PROPERTY_OWNER, null);

        // Act
        Response response = userResource.updateUserDetails(userId, updatedUser);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUserDetailsForbidden() {
        // Arrange
        User currentUser = new User(2L, "FirstName2", "LastName2", "1234567890", "mail2@mail.mail", "username2", "password", Boolean.TRUE, UserTypeEnum.PROPERTY_OWNER, null);
        mockCurrentUser(currentUser);
        Long userId = 1L;
        User updatedUser = new User(userId, "FirstName2", "LastName2", "1234567890", "mail2@mail.mail", "username2", "password", Boolean.TRUE, UserTypeEnum.PROPERTY_OWNER, null);

        // Act
        Response response = userResource.updateUserDetails(userId, updatedUser);

        // Assert
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertEquals("You are not allowed to access this resource", response.getEntity());
    }

}
