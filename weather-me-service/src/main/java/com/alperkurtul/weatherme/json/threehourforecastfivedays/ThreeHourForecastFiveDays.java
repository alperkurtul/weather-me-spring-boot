package com.alperkurtul.weatherme.json.threehourforecastfivedays;

public class ThreeHourForecastFiveDays {

    private String cod;
    private String message;
    private String cnt;
    private ForecastInfo[] list;
    private CityInfo city;

    public String getCod() {
        return cod;
    }

    public CityInfo getCity() {
        return city;
    }

    public void setCity(CityInfo city) {
        this.city = city;
    }

    public ForecastInfo[] getList() {
        return list;
    }

    public void setList(ForecastInfo[] list) {
        this.list = list;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }
    
}
