package com.alperkurtul.weatherme.model;

import java.util.ArrayList;

public class WeatherMeDto {

    private String language;
    private String units;

    private String description;
    private String descriptionIcon;
    private String realTemprature;
    private String feelsTemprature;
    private String minTemprature;
    private String maxTemprature;
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

    public String getDescriptionIcon() {
        return descriptionIcon;
    }

    public void setDescriptionIcon(String descriptionIcon) {
        this.descriptionIcon = descriptionIcon;
    }

    public String getRealTemprature() {
        return realTemprature;
    }

    public void setRealTemprature(String realTemprature) {
        this.realTemprature = realTemprature;
    }

    public String getFeelsTemprature() {
        return feelsTemprature;
    }

    public void setFeelsTemprature(String feelsTemprature) {
        this.feelsTemprature = feelsTemprature;
    }

    public String getMinTemprature() {
        return minTemprature;
    }

    public void setMinTemprature(String minTemprature) {
        this.minTemprature = minTemprature;
    }

    public String getMaxTemprature() {
        return maxTemprature;
    }

    public void setMaxTemprature(String maxTemprature) {
        this.maxTemprature = maxTemprature;
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
