package com.alperkurtul.weatherme.repository;

import com.alperkurtul.weatherme.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    List<Location> findAllByLowerCaseLocationNameIsContaining(String lowerCaseLocationName);

    List<Location> findAllByLowerCaseLocationNameIsContainingOrderByLowerCaseLocationName(String lowerCaseLocationName);

    List<Location> findAllByLowerCaseLocationNameStartsWithOrderByLowerCaseLocationName(String lowerCaseLocationName);

}
