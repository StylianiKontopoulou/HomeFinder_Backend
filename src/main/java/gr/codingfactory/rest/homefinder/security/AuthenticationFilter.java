package gr.codingfactory.rest.homefinder.security;

import gr.codingfactory.rest.homefinder.models.User;
import gr.codingfactory.rest.homefinder.repositories.UserRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

@Provider
public class AuthenticationFilter implements jakarta.ws.rs.container.ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpServletRequest httpRequest;
    @Inject
    private UserRepository userRepository;
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    @Override
    public void filter(ContainerRequestContext requestContext) {

        Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(PermitAll.class) || !method.isAnnotationPresent(RolesAllowed.class)) {
            return;
        }
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        if (authorization == null || authorization.isEmpty()) {
            requestContext
                    .abortWith(Response
                            .status(Response.Status.UNAUTHORIZED)
                            .entity("You cannot access this resource")
                            .build());
            return;
        }

        // Example header:"Authorization": "Basic dGVzdHVzZXI6dGVzdHBhc3N3b3Jk"
        final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
        String usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword.getBytes()));

        // Example usernameAndPassword = "testuser:testpassword"
        //Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        // Retrieve user from repository 
        User user = userRepository.findByUsername(username);

        // If user does not exist or given password does not match
        // return unauthorized response code
        if (user == null || !user.getPassword().equals(password)) {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("You cannot access this resource")
                    .build());
            return;
        }

        // At this point, user exists in the database and password matches
        // Check if input user's type has permission to access the resource
        RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);

        Set<String> rolesSetForTheResource = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

        if (!rolesSetForTheResource.contains(user.getUserType().toString())
                && !rolesSetForTheResource.contains("ALL")) {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("You cannot access this resource")
                    .build());
            return;
        }

        // At this point, user's role type is allowed to access the method
        // Inject authenticated user to request context
        httpRequest.setAttribute("currentUser", user);
    }
}
