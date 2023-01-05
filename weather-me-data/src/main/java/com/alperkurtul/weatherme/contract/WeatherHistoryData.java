package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.WeatherHistory;

import java.time.LocalDateTime;

public interface WeatherHistoryData {

    void create(WeatherHistory weatherHistory) throws Exception ;

    Long calculateCallCountSinceGivenHistoryCreateTime(LocalDateTime localDateTime) throws Exception ;

}
