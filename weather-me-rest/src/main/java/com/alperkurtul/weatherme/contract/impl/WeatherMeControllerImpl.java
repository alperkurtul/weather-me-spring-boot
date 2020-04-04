package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.mapper.RestMapper;
import com.alperkurtul.weatherme.model.*;
import com.alperkurtul.weatherme.contract.WeatherMeService;
import com.alperkurtul.weatherme.contract.WeatherMeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class WeatherMeControllerImpl implements WeatherMeController {

    @Autowired
    private WeatherMeService weatherMeService;

    private RestMapper restMapper = RestMapper.INSTANCE;

    @Override
    public CurrentWeatherResponse getCurrentWeather( String locationId) throws Exception {

        WeatherMeRequest weatherMeRequest = new WeatherMeRequest();
        weatherMeRequest.setLocationId(locationId);

        WeatherMeDto weatherMeDtoInput = restMapper.toWeatherMeDto(weatherMeRequest);

        WeatherMeDto weatherMeDtoOutput = weatherMeService.getCurrentWeather(weatherMeDtoInput);

        CurrentWeatherResponse currentWeatherResponse = restMapper.toCurrentWeatherResponse(weatherMeDtoOutput);

        return currentWeatherResponse;

    }

    @Override
    public List<LocationResponse> getLocationList(String locationName) throws Exception {

        String language = "tr";
        List<LocationDto> locationDtoList = weatherMeService.findAllLocationByLocationName(locationName, language);

        List<LocationResponse> locationResponseList = new ArrayList<>();
        for (LocationDto locationDto : locationDtoList) {
            LocationResponse locationResponse = restMapper.toLocationResponse(locationDto);
            locationResponseList.add(locationResponse);
        }

        return locationResponseList;
    }


}
