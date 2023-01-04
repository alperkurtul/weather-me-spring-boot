package com.alperkurtul.weatherme.json.threehourforecastfivedays;

public class SnowInfo {

    private String a1h;
    private String a3h;

    public String get3h() {
        return a3h;
    }

    public String get1h() {
        return a1h;
    }

    public void set1h(String a1h) {
        this.a1h = a1h;
    }

    public void set3h(String a3h) {
        this.a3h = a3h;
    }

}
