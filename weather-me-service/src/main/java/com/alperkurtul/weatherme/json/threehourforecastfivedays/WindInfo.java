package com.alperkurtul.weatherme.json.threehourforecastfivedays;

public class WindInfo {

    private String speed;
    private String deg;
    private String gust;

    public String getSpeed() {
        return speed;
    }

    public String getDeg() {
        return deg;
    }

    public void setDeg(String deg) {
        this.deg = deg;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getGust() {
        return gust;
    }

    public void setGust(String gust) {
        this.gust = gust;
    }

}
