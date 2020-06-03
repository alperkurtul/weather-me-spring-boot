package com.alperkurtul.weatherme.repository;

import com.alperkurtul.weatherme.model.WeatherHistory;
import com.alperkurtul.weatherme.model.WeatherHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherHistoryRepository extends JpaRepository<WeatherHistory, WeatherHistoryId> {
}
