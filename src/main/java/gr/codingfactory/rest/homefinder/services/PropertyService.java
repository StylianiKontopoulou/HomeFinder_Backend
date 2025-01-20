package gr.codingfactory.rest.homefinder.services;

import gr.codingfactory.rest.homefinder.models.Property;
import java.util.List;


public interface PropertyService {

    List<Property> getAllProperties();

    List<Property> searchProperties(String location, String type);

    Property getPropertyById(Long propertyId);

    Property createProperty(Property property);

    void updateProperty(Long propertyId, Property property);

    void deleteProperty(Long propertyId);

    List<Property> getPropertiesByUserId(Long userId);
}
