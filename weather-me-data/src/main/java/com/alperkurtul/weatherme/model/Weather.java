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

    @Column(name = "WeatherJson", length = 4096)
    private String weatherJson;

    @Column(name = "RequestUrl")
    private String requestUrl;

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

    public String getWeatherJson() {
        return weatherJson;
    }

    public void setWeatherJson(String weatherJson) {
        this.weatherJson = weatherJson;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
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
