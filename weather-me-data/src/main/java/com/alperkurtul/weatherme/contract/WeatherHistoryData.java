package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.WeatherHistory;

public interface WeatherHistoryData {

    void save(WeatherHistory weatherHistory) throws Exception ;

}
