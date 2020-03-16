package com.alperkurtul.weatherme.data;

import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherData {

    @Autowired
    private WeatherRepository weatherRepository;

    public void saveWeather(Weather weather) {

        weatherRepository.save(weather);

    }

}
