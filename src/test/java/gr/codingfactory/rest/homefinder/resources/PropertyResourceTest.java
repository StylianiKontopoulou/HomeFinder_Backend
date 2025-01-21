package gr.codingfactory.rest.homefinder.resources;

import gr.codingfactory.rest.homefinder.resources.PropertyResource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import gr.codingfactory.rest.homefinder.models.Property;
import gr.codingfactory.rest.homefinder.models.User;
import gr.codingfactory.rest.homefinder.models.UserTypeEnum;
import gr.codingfactory.rest.homefinder.services.PropertyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyResourceTest {

    @InjectMocks
    private PropertyResource propertyResource;

    @Mock
    private PropertyService propertyService;

    @Mock
    private HttpServletRequest requestContext;

    private User currentUser;
    private Property testProperty;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        currentUser = new User(1L, "FirstName", "LastName", "1234567890", "mail@mail.mail", "username", "password", Boolean.TRUE, UserTypeEnum.PROPERTY_OWNER, null);
        testProperty = Property.builder().id(1L).user(currentUser).build();
    }

    @Test
    public void testCreateProperty() {
        // Arrange
        Property newProperty = Property.builder().id(1L).user(currentUser).build();
        when(propertyService.createProperty(any(Property.class))).thenReturn(testProperty);
        when(requestContext.getAttribute("currentUser")).thenReturn(currentUser);
        // Act
        Response response = propertyResource.createProperty(newProperty);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(testProperty, response.getEntity());
    }

    @Test
    public void testGetPropertyDetails() {
        // Arrange
        Long propertyId = 1L;
        when(propertyService.getPropertyById(propertyId)).thenReturn(testProperty);
        when(requestContext.getAttribute("currentUser")).thenReturn(currentUser);
        
        // Act
        Response response = propertyResource.getPropertyDetails(propertyId);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(testProperty, response.getEntity());
    }

    @Test
    public void testGetPropertyDetailsForbidden() {
        // Arrange
        User otherUser = new User(2L, "FirstName", "LastName", "1234567890", "mail2@mail.mail", "username2", "password", Boolean.TRUE, UserTypeEnum.PROPERTY_OWNER, null);
        when(requestContext.getAttribute("currentUser")).thenReturn(otherUser);
        Long propertyId = 1L;
        when(propertyService.getPropertyById(propertyId)).thenReturn(testProperty);
        // Act
        Response response = propertyResource.getPropertyDetails(propertyId);

        // Assert
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertEquals("You are not allowed to access this resource", response.getEntity());
    }

    @Test
    public void testUpdatePropertyDetails() {
        // Arrange
        Property updatedProperty = Property.builder().id(1L).user(currentUser).build();
        when(requestContext.getAttribute("currentUser")).thenReturn(currentUser);
        
        // Act
        Response response = propertyResource.updatePropertyDetails(1L, updatedProperty);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteProperty() {
        // Arrange
        Long propertyId = 1L;
        when(propertyService.getPropertyById(propertyId)).thenReturn(testProperty);
        when(requestContext.getAttribute("currentUser")).thenReturn(currentUser);
        
        // Act
        Response response = propertyResource.deleteProperty(propertyId);

        // Assert
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeletePropertyNotFound() {
        // Arrange
        Long propertyId = 1L;
        when(propertyService.getPropertyById(propertyId)).thenReturn(null);

        // Act
        Response response = propertyResource.deleteProperty(propertyId);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeletePropertyForbidden() {
        // Arrange
        User otherUser = new User(2L, "FirstName", "LastName", "1234567890", "mail2@mail.mail", "username2", "password", Boolean.TRUE, UserTypeEnum.PROPERTY_OWNER, null);
        when(requestContext.getAttribute("currentUser")).thenReturn(otherUser);
        Long propertyId = 1L;
        when(propertyService.getPropertyById(propertyId)).thenReturn(testProperty);

        // Act
        Response response = propertyResource.deleteProperty(propertyId);

        // Assert
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertEquals("You are not allowed to access this resource", response.getEntity());
    }

    @Test
    public void testListProperties() {
        // Arrange
        when(propertyService.getAllProperties()).thenReturn(List.of(testProperty));

        // Act
        Response response = propertyResource.listProperties(Optional.empty());

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(((List<?>) response.getEntity()).contains(testProperty));
    }

    @Test
    public void testListPropertiesByUserId() {
        // Arrange
        Long userId = 1L;
        when(propertyService.getPropertiesByUserId(userId)).thenReturn(List.of(testProperty));

        // Act
        Response response = propertyResource.listProperties(Optional.of(userId));

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(((List<?>) response.getEntity()).contains(testProperty));
    }
}
