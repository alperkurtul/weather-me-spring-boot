package com.alperkurtul.weatherme.repository;

import com.alperkurtul.weatherme.model.WeatherHistory;
import com.alperkurtul.weatherme.model.WeatherHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherHistoryRepository extends JpaRepository<WeatherHistory, WeatherHistoryId> {


    @Query("SELECT COUNT(wh) FROM WeatherHistory wh WHERE wh.weatherHistoryId.historyCreateTime >= ?1 AND wh.apiCalledFlag = true")
    Long calculateCallCountSinceGivenHistoryCreateTime(LocalDateTime historyCreateTime);

    @Query(value = "SELECT * " +
            "FROM WeatherHistory wh " +
            "WHERE wh.locationId = ?1 AND " +
            "wh.language = ?2 AND " +
            "wh.units = ?3 AND " +
            "wh.historyCreateTime = ?4 " +
            "ORDER BY wh.counterForHistoryCreateTime DESC " +
            "LIMIT 1 ", nativeQuery = true)
    Optional<WeatherHistory> maxId(int locationId, String language, String units, LocalDateTime historyCreateTime);

}
