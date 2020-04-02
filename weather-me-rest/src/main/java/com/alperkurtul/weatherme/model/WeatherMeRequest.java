package com.alperkurtul.weatherme.model;

public class WeatherMeRequest {

    private String locationId;
    private String locationName;
    private String language;
    private String units;

    public WeatherMeRequest(String locationId, String locationName, String language, String units) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.language = language;
        this.units = units;
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
