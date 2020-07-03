package com.alperkurtul.weatherme.repository;

import com.alperkurtul.weatherme.model.WeatherHistory;
import com.alperkurtul.weatherme.model.WeatherHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface WeatherHistoryRepository extends JpaRepository<WeatherHistory, WeatherHistoryId> {

    @Query("SELECT COUNT(wh) FROM WeatherHistory wh WHERE wh.weatherHistoryId.historyCreateTime >= ?1 AND wh.apiCalledFlag = true")
    Long calculateCallCount(LocalDateTime localDateTime);

}
