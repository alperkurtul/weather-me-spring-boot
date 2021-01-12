package com.alperkurtul.weatherme.model;

public class WeatherNearFutureResponse {

    private String temp;
    private String description;
    private String dtTxt;

    public String getTemp() {
        return temp;
    }

    public String getDtTxt() {
        return dtTxt;
    }

    public void setDtTxt(String dtTxt) {
        this.dtTxt = dtTxt;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
