package com.alperkurtul.weatherme.data;

import com.alperkurtul.weatherme.data.model.Weather;
import com.alperkurtul.weatherme.data.repository.WeatherRepository;
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
