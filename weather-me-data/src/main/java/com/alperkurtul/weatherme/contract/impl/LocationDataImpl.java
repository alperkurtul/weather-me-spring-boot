package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.contract.LocationData;
import com.alperkurtul.weatherme.error.ErrorContants;
import com.alperkurtul.weatherme.error.exception.EntityAlreadyExistException;
import com.alperkurtul.weatherme.error.exception.EntityNotFoundException;
import com.alperkurtul.weatherme.model.Location;
import com.alperkurtul.weatherme.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationDataImpl implements LocationData {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public void create(Location location) throws Exception {

        Optional<Location> optionalLocation = locationRepository.findById(location.getLocationId());
        if (optionalLocation.isPresent()) {
            throw new EntityAlreadyExistException(new Exception("Error in Location model"), ErrorContants.REASON_CODE_ENTITY_ALREADY_EXIST);
        }

        locationRepository.save(location);
    }

    @Override
    public void update(Location location) throws Exception {

        Optional<Location> optionalWeather = locationRepository.findById(location.getLocationId());
        if (!optionalWeather.isPresent()) {
            throw new EntityNotFoundException(new Exception("Error in Location model"), ErrorContants.REASON_CODE_ENTITY_NOT_FOUND);
        }

        locationRepository.save(location);
    }

    @Override
    public Optional<Location> findById(Integer locationId) throws Exception {
        return locationRepository.findById(locationId);
    }

}