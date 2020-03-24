package com.alperkurtul.weatherme.bean;

public class WeatherMeRequest {

    private String locationName;
    private String language;
    private String units;

    public WeatherMeRequest(String locationName, String language, String units) {
        this.locationName = locationName;
        this.language = language;
        this.units = units;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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
}
