package com.alperkurtul.weatherme.repository;

import com.alperkurtul.weatherme.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
