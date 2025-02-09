package gr.codingfactory.rest.homefinder;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Operation;

import org.eclipse.microprofile.openapi.models.OpenAPI;
/**
 * A filter to make final configuration changes to the produced OpenAPI
 * document.
 */

@ApplicationScoped
public class OpenApiFilter implements OASFilter {

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        if (openAPI.getPaths() != null) {
            openAPI.getPaths().removePathItem("/application.wadl");
            openAPI.getPaths().removePathItem("/application.wadl/{path}");
        }
    }

}