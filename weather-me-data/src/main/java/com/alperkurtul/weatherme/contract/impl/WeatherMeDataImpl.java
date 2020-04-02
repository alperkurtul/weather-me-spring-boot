package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.contract.WeatherMeData;
import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherId;
import com.alperkurtul.weatherme.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WeatherMeDataImpl implements WeatherMeData {

    @Autowired
    private WeatherRepository weatherRepository;

    @Override
    public void save(Weather weather) throws Exception {
        weatherRepository.save(weather);
    }

    @Override
    public Optional<Weather> findById(WeatherId weatherId) throws Exception {
        return weatherRepository.findById(weatherId);
    }

}
