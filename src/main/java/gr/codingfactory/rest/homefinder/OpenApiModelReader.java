package gr.codingfactory.rest.homefinder;


import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASModelReader;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.info.Info;

@ApplicationScoped
public class OpenApiModelReader implements OASModelReader {
    @Override
    public OpenAPI buildModel() {
      return OASFactory.createObject(OpenAPI.class)
              .info(OASFactory.createObject(Info.class)
                      .title("HomeFinder API").version("1.0"));
    }
}
