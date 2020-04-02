package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.mapper.RestMapper;
import com.alperkurtul.weatherme.model.CurrentWeatherResponse;
import com.alperkurtul.weatherme.model.WeatherMeDto;
import com.alperkurtul.weatherme.model.WeatherMeRequest;
import com.alperkurtul.weatherme.contract.WeatherMeService;
import com.alperkurtul.weatherme.contract.WeatherMeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherMeControllerImpl implements WeatherMeController {

    @Autowired
    private WeatherMeService weatherMeService;

    private RestMapper restMapper = RestMapper.INSTANCE;

    @Override
    public CurrentWeatherResponse getCurrentWeather(String key) throws Exception {

        WeatherMeRequest weatherMeRequest = new WeatherMeRequest("","Istanbul", "tr", "metric");

        WeatherMeDto weatherMeDtoInput = restMapper.toWeatherMeDto(weatherMeRequest);

        WeatherMeDto weatherMeDtoOutput = weatherMeService.getCurrentWeather(weatherMeDtoInput);

        CurrentWeatherResponse currentWeatherResponse = restMapper.toCurrentWeatherResponse(weatherMeDtoOutput);

        return currentWeatherResponse;

    }

    @Override
    public CurrentWeatherResponse findById(WeatherMeRequest request) throws Exception {

        WeatherMeDto weatherMeDtoInput = restMapper.toWeatherMeDto(request);

        WeatherMeDto weatherMeDtoOutput = weatherMeService.findById(weatherMeDtoInput);

        CurrentWeatherResponse currentWeatherResponse = restMapper.toCurrentWeatherResponse(weatherMeDtoOutput);

        return currentWeatherResponse;

    }

}
