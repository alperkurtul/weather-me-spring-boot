package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.bean.CurrentWeatherResponse;
import com.alperkurtul.weatherme.bean.WeatherMeRequest;
import com.alperkurtul.weatherme.contract.WeatherMeService;
import com.alperkurtul.weatherme.contract.WeatherMeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherMeControllerImpl implements WeatherMeController {

    @Autowired
    private WeatherMeService weatherMeService;

    @Override
    public CurrentWeatherResponse getTemplate(String key) throws Exception {

        CurrentWeatherResponse currentWeatherResponse = (CurrentWeatherResponse) weatherMeService.getCurrentWeather(new WeatherMeRequest("Istanbul", "tr", "metric"));

        return currentWeatherResponse;
    }

    @Override
    public CurrentWeatherResponse setTemplate(WeatherMeRequest request) throws Exception {

        CurrentWeatherResponse currentWeatherResponse = (CurrentWeatherResponse) weatherMeService.getCurrentWeather(new WeatherMeRequest("Istanbul", "tr", "metric"));

        return currentWeatherResponse;
    }

}
