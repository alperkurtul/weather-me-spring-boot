package com.alperkurtul.weatherme.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "IDX_HistoryCreateTime", columnList = "HistoryCreateTime"),
        @Index(name = "IDX_HistCrt_ApiCall", columnList = "HistoryCreateTime,ApiCalledFlag")
})
public class WeatherHistory {

    @EmbeddedId
    private WeatherHistoryId weatherHistoryId;

    @Column(name = "LocationName")
    private String locationName;

    @Column(name = "WeatherRequest")
    private String weatherRequest;

    @Column(name = "WeatherResponse", length = 4096)
    private String weatherResponse;

    @Column(name = "ForecastRequest")
    private String forecastRequest;

    @Column(name = "ForecastResponse", length = 4096)
    private String forecastResponse;

    @Column(name="CreateTime")
    private LocalDateTime createTime;

    @Column(name="UpdateTime")
    private LocalDateTime updateTime;

    @Column(name="ApiCalledFlag")
    private boolean apiCalledFlag;

    public WeatherHistoryId getWeatherHistoryId() {
        return weatherHistoryId;
    }

    public void setWeatherHistoryId(WeatherHistoryId weatherHistoryId) {
        this.weatherHistoryId = weatherHistoryId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getWeatherRequest() {
        return weatherRequest;
    }

    public void setWeatherRequest(String weatherRequest) {
        this.weatherRequest = weatherRequest;
    }

    public String getWeatherResponse() {
        return weatherResponse;
    }

    public void setWeatherResponse(String weatherResponse) {
        this.weatherResponse = weatherResponse;
    }

    public String getForecastRequest() {
        return forecastRequest;
    }

    public void setForecastRequest(String forecastRequest) {
        this.forecastRequest = forecastRequest;
    }

    public String getForecastResponse() {
        return forecastResponse;
    }

    public void setForecastResponse(String forecastResponse) {
        this.forecastResponse = forecastResponse;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public boolean getApiCalledFlag() {
        return apiCalledFlag;
    }

    public void setApiCalledFlag(boolean apiCalledFlag) {
        this.apiCalledFlag = apiCalledFlag;
    }
    
}
