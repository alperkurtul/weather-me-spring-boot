package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.mapper.RestMapper;
import com.alperkurtul.weatherme.model.CurrentWeatherResponse;
import com.alperkurtul.weatherme.model.LocationDto;
import com.alperkurtul.weatherme.model.LocationModel;
import com.alperkurtul.weatherme.model.LocationResp;
import com.alperkurtul.weatherme.model.LocationResponse;
import com.alperkurtul.weatherme.model.WeatherMeDto;
import com.alperkurtul.weatherme.model.WeatherMeRequest;
import com.alperkurtul.weatherme.contract.WeatherMeService;
import com.alperkurtul.weatherme.contract.WeatherMeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
public class WeatherMeControllerImpl implements WeatherMeController {

    @Autowired
    private WeatherMeService weatherMeService;

    private RestMapper restMapper = RestMapper.INSTANCE;

    @Override
    public CurrentWeatherResponse getCurrentWeather(String locationId) throws Exception {

        WeatherMeRequest weatherMeRequest = new WeatherMeRequest();
        weatherMeRequest.setLocationId(locationId);

        WeatherMeDto weatherMeDtoInput = restMapper.toWeatherMeDto(weatherMeRequest);

        WeatherMeDto weatherMeDtoOutput = weatherMeService.getCurrentWeather(weatherMeDtoInput);

        CurrentWeatherResponse currentWeatherResponse = restMapper.toCurrentWeatherResponse(weatherMeDtoOutput);

        return currentWeatherResponse;

    }

    @Override
    public LocationResponse getLocationList(String locationName) throws Exception {

        String language = "tr";

        LocationDto locationDto = weatherMeService.findAllLocationByLocationName(locationName, language);

        LocationResponse locationResponse = new LocationResponse();
        locationResponse = restMapper.toLocationResponse(locationDto);

        List<LocationResp> locationResps = new ArrayList<>();
        List<LocationModel> locationModels = locationDto.getLocationModelList();
        for (LocationModel locationModel : locationModels) {
            LocationResp locationResp = restMapper.toLocationResp(locationModel);
            locationResps.add(locationResp);
        }

        locationResponse.setLocationRespList(locationResps);

        return locationResponse;
    }

    @Override
    public Boolean loadLocationsToDb() throws Exception {
        return weatherMeService.loadLocationsToDb();
    }

    @Override
    public String developmentThings() throws Exception {

        String a = "START";

        Locale[] locales = Locale.getAvailableLocales();

        return "THE END!!!!";
    }

}
