package gr.codingfactory.rest.homefinder.resources;

import gr.codingfactory.rest.homefinder.models.Property;
import gr.codingfactory.rest.homefinder.models.User;
import gr.codingfactory.rest.homefinder.models.UserTypeEnum;
import gr.codingfactory.rest.homefinder.services.PropertyService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/properties")
public class PropertyResource {

    @Inject
    private PropertyService propertyService;

    @Context
    private HttpServletRequest requestContext;

    @POST
    @RolesAllowed("ALL")
    public Response createProperty(Property req) {
        try {
            
            User user = (User) requestContext.getAttribute("currentUser");
            req.setUser(user);
            req.setIsActive(Boolean.TRUE);
            req.setCreatedAt(new Date());
            Property property = propertyService.createProperty(req);
            return Response.status(Response.Status.CREATED).entity(property).build();
        } catch (Exception e) {
            log.error("Error creating property", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{propertyId}")
    @RolesAllowed("ALL")
    public Response getPropertyDetails(@PathParam("propertyId") Long propertyId) {
        try {
            Property property = propertyService.getPropertyById(propertyId);

            if (property != null) {
                User user = (User) requestContext.getAttribute("currentUser");
                if (user.getUserType().equals(UserTypeEnum.PROPERTY_OWNER) && !property.getUser().getId().equals(user.getId())) {
                    return Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to access this resource").build();
                }
                return Response.ok(property).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            log.error("Error getting property details", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{propertyId}")
    @RolesAllowed("ALL")
    public Response updatePropertyDetails(@PathParam("propertyId") Long propertyId, Property property) {
        try {
            User user = (User) requestContext.getAttribute("currentUser");
            property.setUser(user);
            property.setIsActive(Boolean.TRUE);
            propertyService.updateProperty(propertyId, property);
            return Response.ok().build();
        } catch (Exception e) {
            log.error("Error updating property details", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{propertyId}")
    @RolesAllowed("ALL")
    public Response deleteProperty(@PathParam("propertyId") Long propertyId) {
        Property property = propertyService.getPropertyById(propertyId);
        if (property == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User user = (User) requestContext.getAttribute("currentUser");
        if (user.getUserType().equals(UserTypeEnum.PROPERTY_OWNER) && !property.getUser().getId().equals(user.getId())) {
            return Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to access this resource").build();
        }
        try {
            propertyService.deleteProperty(propertyId);
            return Response.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting property", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response listProperties(@QueryParam("userId") Optional<Long> userId) {
        try {
            List<Property> properties;
            if (userId.isPresent()) {
                properties = propertyService.getPropertiesByUserId(userId.get());
            } else {
                properties = propertyService.getAllProperties();
            }
            return Response.ok(properties).build();
        } catch (Exception e) {
            log.error("Error listing owner properties", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
