package com.alperkurtul.weatherme.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class WeatherHistoryId implements Serializable {
    @Column(name = "LocationId")
    private int locationId;
    @Column(name = "Language", length = 10)
    private String language;
    @Column(name = "Units", length = 10)
    private String units;
    @Column(name="HistoryCreateTime")
    private LocalDateTime historyCreateTime;

    public WeatherHistoryId() {
    }

    public WeatherHistoryId(int locationId, String language, String units, LocalDateTime historyCreateTime) {
        this.locationId = locationId;
        this.language = language;
        this.units = units;
        this.historyCreateTime = historyCreateTime;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public LocalDateTime getHistoryCreateTime() {
        return historyCreateTime;
    }

    public void setHistoryCreateTime(LocalDateTime historyCreateTime) {
        this.historyCreateTime = historyCreateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherHistoryId that = (WeatherHistoryId) o;
        return locationId == that.locationId &&
                Objects.equals(language, that.language) &&
                Objects.equals(units, that.units) &&
                Objects.equals(historyCreateTime, that.historyCreateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, language, units, historyCreateTime);
    }

}
