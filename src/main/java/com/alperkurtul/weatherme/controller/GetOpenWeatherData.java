package com.alperkurtul.weatherme.controller;

import com.alperkurtul.weatherme.bean.WeatherDataBean;
import com.alperkurtul.weatherme.service.GetDataFromOpenWeather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wm")
public class GetOpenWeatherData {

    @Autowired
    GetDataFromOpenWeather currentWeather;

    @RequestMapping(value = "/curwet", method = RequestMethod.GET)
    public WeatherDataBean getCurrentWeather() {

        WeatherDataBean weatherDataBean = currentWeather.getCurrentWeather();

        return weatherDataBean;
    }

}
