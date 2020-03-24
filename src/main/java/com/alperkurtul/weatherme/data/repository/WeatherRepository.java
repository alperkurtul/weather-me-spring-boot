package com.alperkurtul.weatherme.data.repository;

import com.alperkurtul.weatherme.data.model.Weather;
import com.alperkurtul.weatherme.data.model.WeatherId;
import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<Weather, WeatherId> {
}
