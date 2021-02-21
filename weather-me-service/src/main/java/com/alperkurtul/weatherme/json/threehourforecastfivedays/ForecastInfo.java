package com.alperkurtul.weatherme.json.threehourforecastfivedays;

public class ForecastInfo {

    private String dt;
    private MainInfo main;
    private WeatherInfo[] weather;
    private CloudsInfo clouds;
    private WindInfo wind;
    private String visibility;
    private String pop;
    private RainInfo rain;
    private SnowInfo snow;
    private SysInfo sys;
    private String dt_txt;

    public String getDt() {
        return dt;
    }

    public SnowInfo getSnow() {
        return snow;
    }

    public void setSnow(SnowInfo snow) {
        this.snow = snow;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }

    public SysInfo getSys() {
        return sys;
    }

    public void setSys(SysInfo sys) {
        this.sys = sys;
    }

    public RainInfo getRain() {
        return rain;
    }

    public void setRain(RainInfo rain) {
        this.rain = rain;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
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

    public WeatherInfo[] getWeather() {
        return weather;
    }

    public void setWeather(WeatherInfo[] weather) {
        this.weather = weather;
    }

    public MainInfo getMain() {
        return main;
    }

    public void setMain(MainInfo main) {
        this.main = main;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

}
