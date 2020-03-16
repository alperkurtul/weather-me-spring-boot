package com.alperkurtul.weatherme.rest;

import com.alperkurtul.weatherme.bean.CurrentWeatherDataBean;
import com.alperkurtul.weatherme.bean.WeatherRequestParametersBean;
import com.alperkurtul.weatherme.service.GetDataFromOpenWeather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wm")
@CrossOrigin("*")
public class GetOpenWeatherData {

    @Autowired
    private GetDataFromOpenWeather currentWeather;

    @RequestMapping(value = "/curwet", method = RequestMethod.GET)
    public CurrentWeatherDataBean getCurrentWeather() {

        CurrentWeatherDataBean currentWeatherDataBean = (CurrentWeatherDataBean) currentWeather.getCurrentWeather(new WeatherRequestParametersBean("Istanbul","tr","metric"));

        return currentWeatherDataBean;
    }

}
