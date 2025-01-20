package gr.codingfactory.rest.homefinder.services;

import gr.codingfactory.rest.homefinder.models.Property;
import gr.codingfactory.rest.homefinder.repositories.PropertyRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.List;

@RequestScoped
public class PropertyServiceImpl implements PropertyService {

    @Inject
    private PropertyRepository propertyRepository;

    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public List<Property> searchProperties(String location, String type) {
        return propertyRepository.findPropertiesByCriteria(location, type);
    }

    @Override
    public Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId).orElse(null);
    }

    @Override
    public Property createProperty(Property property) {
        return propertyRepository.create(property);
    }

    @Override
    public void updateProperty(Long propertyId, Property property) {
        Property existingProperty = propertyRepository.findById(propertyId).orElse(null);
        if (existingProperty != null) {
            existingProperty.setTitle(property.getTitle());
            existingProperty.setDescription(property.getDescription());
            existingProperty.setYearOfConstruction(property.getYearOfConstruction());
            existingProperty.setPrice(property.getPrice());
            existingProperty.setSquareMeters(property.getSquareMeters());
            existingProperty.setFloor(property.getFloor());
            existingProperty.setBathrooms(property.getBathrooms());
            existingProperty.setBedrooms(property.getBedrooms());
            existingProperty.setCondition(property.getCondition());
            existingProperty.setEnergyClass(property.getEnergyClass());
            existingProperty.setPropertyType(property.getPropertyType());
            existingProperty.setUser(property.getUser());
            existingProperty.setIsActive(property.getIsActive());
            existingProperty.setImage(property.getImage());
            existingProperty.setArea(property.getArea());
            existingProperty.setPropertyUse(property.getPropertyUse());
            propertyRepository.update(existingProperty);
        }
    }

    @Override
    public void deleteProperty(Long propertyId) {
        propertyRepository.softDelete(propertyId);
    }

    @Override
    public List<Property> getPropertiesByUserId(Long userId) {
        return propertyRepository.findPropertiesByUserId(userId);
    }
}