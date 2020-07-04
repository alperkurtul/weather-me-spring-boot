package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.contract.WeatherData;
import com.alperkurtul.weatherme.error.ErrorContants;
import com.alperkurtul.weatherme.error.exception.EntityAlreadyExistExceptionN43;
import com.alperkurtul.weatherme.error.exception.EntityNotFoundExceptionN10;
import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherId;
import com.alperkurtul.weatherme.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WeatherDataImpl implements WeatherData {

    @Autowired
    private WeatherRepository weatherRepository;

    @Override
    public Weather create(Weather weather) throws Exception {
        LocalDateTime localDateTimeNow;

        Optional<Weather> optionalWeather = weatherRepository.findById(weather.getWeatherId());
        if (optionalWeather.isPresent()) {
            throw new EntityAlreadyExistExceptionN43(new Exception("Error in Weather model"), ErrorContants.REASON_CODE_ENTITY_ALREADY_EXIST);
        }

        localDateTimeNow = LocalDateTime.now();
        if (weather.getCreateTime() == null) {
            weather.setCreateTime(localDateTimeNow);
        }

        if (weather.getUpdateTime() == null) {
            weather.setUpdateTime(localDateTimeNow);
        }
        weatherRepository.save(weather);

        return weather;
    }

    @Override
    public Weather update(Weather weather) throws Exception {

        Optional<Weather> optionalWeather = weatherRepository.findById(weather.getWeatherId());
        if (!optionalWeather.isPresent()) {
            throw new EntityNotFoundExceptionN10(new Exception("Error in Weather model"), ErrorContants.REASON_CODE_ENTITY_NOT_FOUND);
        }

        if (weather.getUpdateTime() == null) {
            weather.setUpdateTime(LocalDateTime.now());
        }
        weatherRepository.save(weather);

        return weather;
    }

    @Override
    public Optional<Weather> findById(WeatherId weatherId) throws Exception {
        return weatherRepository.findById(weatherId);
    }

}
