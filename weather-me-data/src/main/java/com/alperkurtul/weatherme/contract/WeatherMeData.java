package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherId;

import java.util.Optional;

public interface WeatherMeData {

    void saveTemplate(Weather template);

    Optional<Weather> findByIdTemplate(WeatherId weatherId);

}
