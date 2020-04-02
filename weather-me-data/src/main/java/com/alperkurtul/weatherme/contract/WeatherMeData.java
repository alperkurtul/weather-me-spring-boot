package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherId;

import java.util.Optional;

public interface WeatherMeData {

    void save(Weather weather) throws Exception ;

    Optional<Weather> findById(WeatherId weatherId) throws Exception;

}
