package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.LocationDto;
import com.alperkurtul.weatherme.model.WeatherMeDto;

import java.util.List;

public interface WeatherMeService {

    WeatherMeDto getCurrentWeather(WeatherMeDto var1) throws Exception;

    WeatherMeDto findById(WeatherMeDto var1) throws Exception;

    List<LocationDto> findAllLocationByLocationName(String locationName, String language) throws Exception;

    Boolean loadLocationsToDb() throws Exception;

}
