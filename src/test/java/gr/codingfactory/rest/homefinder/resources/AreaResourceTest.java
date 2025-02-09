package gr.codingfactory.rest.homefinder.resources;

import gr.codingfactory.rest.homefinder.resources.AreaResource;
import gr.codingfactory.rest.homefinder.models.Area;
import gr.codingfactory.rest.homefinder.services.AreaService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class AreaResourceTest {

    @Mock
    private AreaService areaService;

    @InjectMocks
    private AreaResource areaResource;

    private List<Area> areas;

    @BeforeEach
    public void setUp() {
        areas = Arrays.asList(
                new Area(1L, "Athens", null),
                new Area(2L, "Thessaloniki", null)
        );
    }

    @Test
    public void testGetAreasSuccess() {
        // Arrange: Mock the behavior of the AreaService
        when(areaService.getAll()).thenReturn(areas);

        // Act: Call the method under test
        Response response = areaResource.getAreas();

        // Assert: Verify the response
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(((List<?>) response.getEntity()).size() > 0);
    }

    @Test
    public void testGetAreasServiceThrowsException() {
        // Arrange: Mock the behavior of the AreaService to throw an exception
        when(areaService.getAll()).thenThrow(new RuntimeException("Database error"));

        // Act: Call the method under test
        Response response = areaResource.getAreas();

        // Assert: Verify the response for error scenario
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Database error", response.getEntity());
    }

    @Test
    public void testGetAreasEmptyList() {
        // Arrange: Mock the behavior of the AreaService to return an empty list
        when(areaService.getAll()).thenReturn(Arrays.asList());

        // Act: Call the method under test
        Response response = areaResource.getAreas();

        // Assert: Verify the response for empty list scenario
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(((List<?>) response.getEntity()).isEmpty());
    }
}
