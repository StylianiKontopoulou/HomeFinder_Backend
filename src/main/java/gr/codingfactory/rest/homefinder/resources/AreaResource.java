package gr.codingfactory.rest.homefinder.resources;

import gr.codingfactory.rest.homefinder.models.Area;
import gr.codingfactory.rest.homefinder.services.AreaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/areas")
@Tag(name = "Areas", description = "Operations related to areas")
public class AreaResource {

    @Inject
    private AreaService areaService;

    @GET
    @Operation(summary = "Retrieve all areas", description = "Returns a list of all available areas.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "List of areas retrieved successfully"),
        @APIResponse(responseCode = "500", description = "Internal server error while retrieving areas")
    })
    public Response getAreas() {
        try {
            List<Area> areas = areaService.getAll();
            return Response.ok(areas).build();
        } catch (Exception e) {
            log.error("An error occurred while retrieving areas", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
