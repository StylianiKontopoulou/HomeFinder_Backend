package gr.codingfactory.rest.homefinder.resources;

import gr.codingfactory.rest.homefinder.models.LoginRequest;
import gr.codingfactory.rest.homefinder.models.User;
import gr.codingfactory.rest.homefinder.models.UserTypeEnum;
import gr.codingfactory.rest.homefinder.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/users")
@Tag(name = "Users", description = "Operations related to user management")
public class UserResource {

    @Inject
    private UserService userService;

    @Context
    private HttpServletRequest requestContext;

    @POST
    @Path("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user and returns the created user.")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "User created successfully"),
        @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response registerUser(User req) {
        try {
            User user = userService.registerUser(req);
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (Exception e) {
            log.error("Error registering user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    @Operation(summary = "Authenticate a user", description = "Validates user credentials and returns user details if authentication is successful.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "User authenticated successfully"),
        @APIResponse(responseCode = "401", description = "Invalid credentials")
    })
    public Response loginUser(LoginRequest req) {
        try {
            User user = userService.authenticate(req.getUserName(), req.getPassword());
            return Response.ok().entity(user).build();
        } catch (Exception e) {
            log.error("Error logging in user", e);
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{userId}")
    @RolesAllowed("ALL")
    @Operation(summary = "Get user details", description = "Retrieves details of a specific user by ID.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "User found"),
        @APIResponse(responseCode = "403", description = "Forbidden access"),
        @APIResponse(responseCode = "404", description = "User not found"),
        @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response getUserDetails(@Parameter(description = "ID of the user to retrieve", required = true) @PathParam("userId") Long userId) {
        User currentUser = (User) requestContext.getAttribute("currentUser");
        if (currentUser.getUserType().equals(UserTypeEnum.PROPERTY_OWNER) && !currentUser.getId().equals(userId)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to access this resource").build();
        }

        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                return Response.ok(user).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            log.error("Error getting user details", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{userId}")
    @RolesAllowed("ALL")
    @Operation(summary = "Update user details", description = "Updates the details of an existing user.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "User updated successfully"),
        @APIResponse(responseCode = "403", description = "Forbidden access"),
        @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response updateUserDetails(@Parameter(description = "ID of the user to update", required = true) @PathParam("userId") Long userId, User user) {
        User currentUser = (User) requestContext.getAttribute("currentUser");
        if (currentUser.getUserType().equals(UserTypeEnum.PROPERTY_OWNER) && !currentUser.getId().equals(userId)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to access this resource").build();
        }

        try {
            userService.updateUser(userId, user);
            return Response.ok().build();
        } catch (Exception e) {
            log.error("Error updating user details", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{userId}")
    @RolesAllowed("ALL")
    @Operation(summary = "Delete a user", description = "Deletes a user by ID.")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "User deleted successfully"),
        @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response deleteUser(@Parameter(description = "ID of the user to delete", required = true) @PathParam("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return Response.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/check_duplicate/{userName}")
    @RolesAllowed("ALL")
    @Operation(summary = "Check if username exists", description = "Checks if a user with the given username already exists.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Returns true if the username exists, otherwise false"),
        @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response checkDuplicateUser(@Parameter(description = "Username to check", required = true) @PathParam("userName") String userName) {
        try {
            User user = userService.getByUsername(userName);
            Boolean userExists = user != null;
            return Response.ok(userExists).build();
        } catch (Exception e) {
            log.error("Error checking duplicate username", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
