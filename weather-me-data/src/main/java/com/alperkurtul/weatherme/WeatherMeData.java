package com.alperkurtul.weatherme;

import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherMeData {

    @Autowired
    private WeatherRepository weatherRepository;

    public void saveTemplate(Weather weather) {

        weatherRepository.save(weather);

    }

}
