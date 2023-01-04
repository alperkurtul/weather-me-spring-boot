package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.contract.WeatherHistoryData;
import com.alperkurtul.weatherme.model.WeatherHistory;
import com.alperkurtul.weatherme.repository.WeatherHistoryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WeatherHistoryDataImpl implements WeatherHistoryData {

    @Autowired
    private WeatherHistoryRepository weatherHistoryRepository;

    private static Logger logger = LogManager.getLogger(WeatherHistoryDataImpl.class);

    @Override
    public void create(WeatherHistory weatherHistory) throws Exception {

        Optional<WeatherHistory> optionalWeatherHistory = weatherHistoryRepository.maxId(
                weatherHistory.getWeatherHistoryId().getLocationId(),
                weatherHistory.getWeatherHistoryId().getLanguage(),
                weatherHistory.getWeatherHistoryId().getUnits(),
                weatherHistory.getWeatherHistoryId().getHistoryCreateTime());
        if (optionalWeatherHistory.isPresent()) {
            weatherHistory.getWeatherHistoryId().setCounterForHistoryCreateTime(optionalWeatherHistory.get().getWeatherHistoryId().getCounterForHistoryCreateTime() + 1);
        } else {
            weatherHistory.getWeatherHistoryId().setCounterForHistoryCreateTime(1);
        }

        int maxTryCount = 100;
        int iterationCount = 0;
        boolean tryAgain = true;
        boolean errorOccured = false;
        while (tryAgain) {
            try {
                iterationCount ++;
                if ( iterationCount == maxTryCount ) {
                    tryAgain = false;
                }
                weatherHistoryRepository.save(weatherHistory);
                tryAgain = false;
            } catch (DataIntegrityViolationException e) {
                errorOccured = true;
                if (tryAgain) {
                    weatherHistory.getWeatherHistoryId().setCounterForHistoryCreateTime(weatherHistory.getWeatherHistoryId().getCounterForHistoryCreateTime() + 1);
                } else {
                    throw new DataIntegrityViolationException(e.getMessage());
                }

            }

        }

        //if (errorOccured) {
            logger.info("WeatherHistory Record has been saved after " + iterationCount + " try." );
        //}

    }

    @Override
    public Long calculateCallCountSinceGivenHistoryCreateTime(LocalDateTime localDateTime) throws Exception {

        Long count = weatherHistoryRepository.calculateCallCountSinceGivenHistoryCreateTime(localDateTime);

        return count;
    }

}
