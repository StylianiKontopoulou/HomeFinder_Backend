package gr.codingfactory.rest.homefinder.resources;

import gr.codingfactory.rest.homefinder.models.Area;
import gr.codingfactory.rest.homefinder.services.AreaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/areas")
public class AreaRersource {

    @Inject
    private AreaService areaService;

    @GET
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
