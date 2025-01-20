package gr.codingfactory.rest.homefinder.resources;

import gr.codingfactory.rest.homefinder.models.LoginRequest;
import gr.codingfactory.rest.homefinder.models.User;
import gr.codingfactory.rest.homefinder.models.UserTypeEnum;
import gr.codingfactory.rest.homefinder.services.UserService;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/users")
public class UserResource {

    @Inject
    private UserService userService;

    @Context
    private HttpServletRequest requestContext;

    @POST
    @Path("/register")
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
    public Response getUserDetails(@PathParam("userId") Long userId) {
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
    public Response updateUserDetails(@PathParam("userId") Long userId, User user) {
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
    public Response deleteUser(@PathParam("userId") Long userId) {
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
    public Response getUserDetails(@PathParam("username") String username) {
        
        try {
            User user = userService.getByUsername(username);
            Boolean userExists = user != null;
            return Response.ok(userExists).build();
        } catch (Exception e) {
            log.error("Error getting user details", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
