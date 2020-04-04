package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.contract.WeatherMeData;
import com.alperkurtul.weatherme.error.ErrorContants;
import com.alperkurtul.weatherme.error.exception.EntityAlreadyExistException;
import com.alperkurtul.weatherme.error.exception.EntityNotFoundException;
import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherId;
import com.alperkurtul.weatherme.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WeatherMeDataImpl implements WeatherMeData {

    @Autowired
    private WeatherRepository weatherRepository;

    @Override
    public void create(Weather weather) throws Exception {

        Optional<Weather> optionalWeather = weatherRepository.findById(weather.getWeatherId());
        if (optionalWeather.isPresent()) {
            throw new EntityAlreadyExistException(null, ErrorContants.REASON_CODE_ENTITY_ALREADY_EXIST);
        }

        if (weather.getCreateTime() == null) {
            weather.setCreateTime(LocalDateTime.now());
        }
        weatherRepository.save(weather);
    }

    @Override
    public void update(Weather weather) throws Exception {

        Optional<Weather> optionalWeather = weatherRepository.findById(weather.getWeatherId());
        if (!optionalWeather.isPresent()) {
            throw new EntityNotFoundException(null, ErrorContants.REASON_CODE_ENTITY_NOT_FOUND);
        }

        if (weather.getCreateTime() == null) {
            weather.setCreateTime(LocalDateTime.now());
        }
        weatherRepository.save(weather);
    }

    @Override
    public Optional<Weather> findById(WeatherId weatherId) throws Exception {
        return weatherRepository.findById(weatherId);
    }

}
