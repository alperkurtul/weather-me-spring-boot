package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.WeatherMeDto;

public interface WeatherMeService {

    WeatherMeDto getCurrentWeather(WeatherMeDto var1);

    WeatherMeDto findByIdTemplate(WeatherMeDto var1);

}
