package com.alperkurtul.weatherme.service;

import com.alperkurtul.weatherme.bean.CurrentWeatherDataBean;
import com.alperkurtul.weatherme.bean.WeatherRequestParametersBean;
import org.springframework.context.annotation.Bean;

public interface GetDataFromOpenWeather {

    @Bean
    public CurrentWeatherDataBean getCurrentWeather(WeatherRequestParametersBean weatherRequestParametersBean);

}
