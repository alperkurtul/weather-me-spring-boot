package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.WeatherMeDto;

public interface WeatherMeService {

    WeatherMeDto getCurrentWeather(WeatherMeDto var1) throws Exception;

    WeatherMeDto findById(WeatherMeDto var1) throws Exception;

    String findLocationNameByLocationId(String locationId) throws Exception;

    String findLocationIdByLocationName(String locationId) throws Exception;

}
