package com.alperkurtul.weatherme.controller;

import com.alperkurtul.weatherme.bean.WeatherDataBean;
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
    GetDataFromOpenWeather currentWeather;

    @RequestMapping(value = "/curwet", method = RequestMethod.GET)
    public WeatherDataBean getCurrentWeather() {

        //WeatherDataBean weatherDataBean = currentWeather.getCurrentWeather();
        WeatherDataBean weatherDataBean = new WeatherDataBean();
        weatherDataBean.setDescription("parçalı az bulutlu");
        weatherDataBean.setDescriptionIcon("http://openweathermap.org/img/w/03d.png");
        weatherDataBean.setRealTemprature("12.93");
        weatherDataBean.setFeelsTemprature("11.87");
        weatherDataBean.setMinTemprature("11.11");
        weatherDataBean.setMaxTemprature("15");
        weatherDataBean.setPressure("1012");
        weatherDataBean.setHumidity("67");
        weatherDataBean.setCountryCode("TR");
        weatherDataBean.setSunRise("09-03-2020 07:25:25");
        weatherDataBean.setSunSet("09-03-2020 19:03:55");
        weatherDataBean.setTimeZone("01-01-1970 03:00:00");
        weatherDataBean.setLocationId("745042");
        weatherDataBean.setLocationName("İstanbul");

        return weatherDataBean;
    }

}
