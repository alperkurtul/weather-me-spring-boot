package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.contract.WeatherHistoryData;
import com.alperkurtul.weatherme.model.WeatherHistory;
import com.alperkurtul.weatherme.repository.WeatherHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WeatherHistoryDataImpl implements WeatherHistoryData {

    @Autowired
    private WeatherHistoryRepository weatherHistoryRepository;

    @Override
    public void save(WeatherHistory weatherHistory) {
        weatherHistoryRepository.save(weatherHistory);
    }

    @Override
    public Long calculateCallCount(LocalDateTime localDateTime) throws Exception {

        Long count = weatherHistoryRepository.calculateCallCount(localDateTime);

        return count;
    }

}
