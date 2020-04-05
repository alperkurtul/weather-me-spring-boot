package com.alperkurtul.weatherme.json.currentweather;

public class CurrentWeather {

    private CWCoordInfo coord;
    private WeatherInfo[] weather;
    private String base;
    private MainInfo main;
    private String visibility;
    private WindInfo wind;
    private CloudsInfo clouds;
    private String dt;
    private SysInfo sys;
    private String timezone;
    private String id;
    private String name;
    private String cod;

    public CWCoordInfo getCoord() {
        return coord;
    }

    public void setCoord(CWCoordInfo coord) {
        this.coord = coord;
    }

    public WeatherInfo[] getWeather() {
        return weather;
    }

    public void setWeather(WeatherInfo[] weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public MainInfo getMain() {
        return main;
    }

    public void setMain(MainInfo main) {
        this.main = main;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public WindInfo getWind() {
        return wind;
    }

    public void setWind(WindInfo wind) {
        this.wind = wind;
    }

    public CloudsInfo getClouds() {
        return clouds;
    }

    public void setClouds(CloudsInfo clouds) {
        this.clouds = clouds;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public SysInfo getSys() {
        return sys;
    }

    public void setSys(SysInfo sys) {
        this.sys = sys;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

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

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }
}
