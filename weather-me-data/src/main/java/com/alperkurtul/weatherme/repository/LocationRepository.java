package com.alperkurtul.weatherme.repository;

import com.alperkurtul.weatherme.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    List<Location> findAllByUpperCaseLocationNameIsContaining(String upperCaseLocationName);

}
