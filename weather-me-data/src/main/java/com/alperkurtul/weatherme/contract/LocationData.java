package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationData {

    void create(Location location) throws Exception ;

    void update(Location location) throws Exception ;

    Optional<Location> findById(Integer locationId) throws Exception;

    List<Location> findAllLocationByLocationName(String locationName) throws Exception;

}
