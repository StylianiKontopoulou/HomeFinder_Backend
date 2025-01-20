package gr.codingfactory.rest.homefinder.services;

import gr.codingfactory.rest.homefinder.models.Area;
import gr.codingfactory.rest.homefinder.repositories.AreaRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.List;

@RequestScoped
public class AreaServiceImpl implements AreaService {

    @Inject
    private AreaRepository areaRepository;

    @Override
    public List<Area> getAll() {
        return areaRepository.findAll();
    }
}