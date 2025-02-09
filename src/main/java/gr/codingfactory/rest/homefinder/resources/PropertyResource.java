package gr.codingfactory.rest.homefinder.resources;

import gr.codingfactory.rest.homefinder.models.Property;
import gr.codingfactory.rest.homefinder.models.User;
import gr.codingfactory.rest.homefinder.models.UserTypeEnum;
import gr.codingfactory.rest.homefinder.services.PropertyService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/properties")
@Tag(name = "Properties", description = "Operations related to property management")
public class PropertyResource {

    @Inject
    private PropertyService propertyService;

    @Context
    private HttpServletRequest requestContext;

    @POST
    @RolesAllowed("ALL")
    @Operation(summary = "Create a new property", description = "Adds a new property to the system.")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Property created successfully"),
        @APIResponse(responseCode = "500", description = "Internal server error while creating property")
    })
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
    @Operation(summary = "Get property details", description = "Retrieves details of a property by its ID.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Successfully retrieved property details"),
        @APIResponse(responseCode = "403", description = "Access forbidden for this property"),
        @APIResponse(responseCode = "404", description = "Property not found"),
        @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response getPropertyDetails(
        @Parameter(description = "ID of the property to retrieve", required = true)
        @PathParam("propertyId") Long propertyId) {
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
    @Operation(summary = "Update property details", description = "Updates the information of an existing property.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Property updated successfully"),
        @APIResponse(responseCode = "500", description = "Internal server error while updating property")
    })
    public Response updatePropertyDetails(
        @Parameter(description = "ID of the property to update", required = true)
        @PathParam("propertyId") Long propertyId,
        Property property) {
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
    @Operation(summary = "Delete a property", description = "Removes a property from the system.")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Property deleted successfully"),
        @APIResponse(responseCode = "403", description = "Access forbidden for this property"),
        @APIResponse(responseCode = "404", description = "Property not found"),
        @APIResponse(responseCode = "500", description = "Internal server error while deleting property")
    })
    public Response deleteProperty(
        @Parameter(description = "ID of the property to delete", required = true)
        @PathParam("propertyId") Long propertyId) {
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
    @Operation(summary = "List all properties", description = "Retrieves a list of properties, optionally filtered by user.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Successfully retrieved list of properties"),
        @APIResponse(responseCode = "500", description = "Internal server error while listing properties")
    })
    public Response listProperties(
        @Parameter(description = "User ID to filter properties by owner", required = false)
        @QueryParam("userId") Optional<Long> userId) {
        try {
            List<Property> properties;
            if (userId.isPresent()) {
                properties = propertyService.getPropertiesByUserId(userId.get());
            } else {
                properties = propertyService.getAllProperties();
            }
            return Response.ok(properties).build();
        } catch (Exception e) {
            log.error("Error listing properties", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
