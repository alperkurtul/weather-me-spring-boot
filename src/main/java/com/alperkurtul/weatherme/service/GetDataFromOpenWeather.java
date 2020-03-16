package com.alperkurtul.weatherme.service;

import org.springframework.context.annotation.Bean;

public interface GetDataFromOpenWeather<REQ, RES> {

    @Bean
    public RES getCurrentWeather(REQ var1);

}
