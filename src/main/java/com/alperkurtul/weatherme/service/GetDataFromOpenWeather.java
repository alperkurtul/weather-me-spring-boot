package com.alperkurtul.weatherme.service;

import com.alperkurtul.weatherme.bean.WeatherDataBean;
import org.springframework.context.annotation.Bean;

public interface GetDataFromOpenWeather {

    @Bean
    public WeatherDataBean getCurrentWeather();

}
