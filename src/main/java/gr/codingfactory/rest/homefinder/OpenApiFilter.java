package gr.codingfactory.rest.homefinder;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Operation;

/**
 * A filter to make final configuration changes to the produced OpenAPI
 * document.
 */

@ApplicationScoped
public class OpenApiFilter implements OASFilter {

    /**
     * Replaces all spaces in each operation id with a hyphen.
     */
    @Override
    public Operation filterOperation(Operation operation) {
        
//        String operationId = operation.getOperationId();
//        if (operationId != null && operationId.contains((" "))) {
//            operation.setOperationId(operationId.replace(" ", "-"));
//        }
        return operation;
    }

}