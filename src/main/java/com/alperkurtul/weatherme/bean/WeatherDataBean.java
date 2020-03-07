package com.alperkurtul.weatherme.bean;

public class WeatherDataBean {

    private String description;
    private String descriptionIcon;
    private String realTemprature;
    private String feelsTemprature;
    private String minTemprature;
    private String maxTemprature;
    private String pressure;
    private String humidity;

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
}
