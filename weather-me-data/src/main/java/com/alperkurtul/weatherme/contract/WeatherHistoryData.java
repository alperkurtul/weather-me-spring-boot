package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.WeatherHistory;

import java.time.LocalDateTime;

public interface WeatherHistoryData {

    void save(WeatherHistory weatherHistory) throws Exception ;

    Long calculateCallCount(LocalDateTime localDateTime) throws Exception ;

}
