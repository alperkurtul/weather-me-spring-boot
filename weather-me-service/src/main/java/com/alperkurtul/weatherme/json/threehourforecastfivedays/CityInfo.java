package com.alperkurtul.weatherme.json.threehourforecastfivedays;

public class CityInfo {

    private String id;
    private String name;
    private CoordinateInfo coord;
    private String country;
    private String population;
    private String timezone;
    private String sunrise;
    private String sunset;

    public String getId() {
        return id;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public CoordinateInfo getCoord() {
        return coord;
    }

    public void setCoord(CoordinateInfo coord) {
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
