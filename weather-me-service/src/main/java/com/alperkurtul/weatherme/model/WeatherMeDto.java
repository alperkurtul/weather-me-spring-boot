package com.alperkurtul.weatherme.model;

import java.util.ArrayList;

public class WeatherMeDto {

    private String language;
    private String units;

    private String id;
    private String main;
    private String description;
    private String icon;
    private String realTemperature;
    private String feelsTemperature;
    private String minTemperature;
    private String maxTemperature;
    private String pressure;
    private String humidity;
    private String countryCode;
    private String sunRise;
    private String sunSet;
    private String timeZone;
    private String locationId;
    private String locationName;
    private ArrayList<WeatherNearFuture> nearFuture;
    private ArrayList<WeatherNextDay> nextDays;

    public String getLanguage() {
        return language;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public ArrayList<WeatherNextDay> getNextDays() {
        return nextDays;
    }

    public void setNextDays(ArrayList<WeatherNextDay> nextDays) {
        this.nextDays = nextDays;
    }

    public ArrayList<WeatherNearFuture> getNearFuture() {
        return nearFuture;
    }

    public void setNearFuture(ArrayList<WeatherNearFuture> nearFuture) {
        this.nearFuture = nearFuture;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRealTemperature() {
        return realTemperature;
    }

    public void setRealTemperature(String realTemperature) {
        this.realTemperature = realTemperature;
    }

    public String getFeelsTemperature() {
        return feelsTemperature;
    }

    public void setFeelsTemperature(String feelsTemperature) {
        this.feelsTemperature = feelsTemperature;
    }

    public String getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(String minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(String maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getSunRise() {
        return sunRise;
    }

    public void setSunRise(String sunRise) {
        this.sunRise = sunRise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public void setSunSet(String sunSet) {
        this.sunSet = sunSet;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
