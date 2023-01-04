package com.alperkurtul.weatherme.json.location;

public class WeatherLocation {

    private String id;
    private String name;
    private String state;
    private String country;
    private WLCoordInfo coord;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public WLCoordInfo getCoord() {
        return coord;
    }

    public void setCoord(WLCoordInfo coord) {
        this.coord = coord;
    }

}
