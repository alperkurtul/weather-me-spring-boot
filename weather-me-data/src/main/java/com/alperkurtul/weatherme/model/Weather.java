package com.alperkurtul.weatherme.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "Weather")
public class Weather {

    @EmbeddedId
    private WeatherId weatherId;

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

    public WeatherId getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(WeatherId weatherId) {
        this.weatherId = weatherId;
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
