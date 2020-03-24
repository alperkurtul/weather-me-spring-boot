package com.alperkurtul.weatherme.contract;

public interface WeatherMeService<REQ, RES> {

    //@Bean
    public RES getCurrentWeather(REQ var1);

}
