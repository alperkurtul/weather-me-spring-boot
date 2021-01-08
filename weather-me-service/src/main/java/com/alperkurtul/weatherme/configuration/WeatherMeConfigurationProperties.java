package com.alperkurtul.weatherme.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "weather.me")
public class WeatherMeConfigurationProperties {

    private String apiAppid;
    private String apiUrl;
    private String apiSuffixForCurrentWeather;
    private String apiSuffixForForecastWeather;
    private String apiCallValidityMinuteForWeather;
    private String apiCallCountLimitPerMinuteForWeather;

    public String getApiAppid() {
        return apiAppid;
    }

    public String getApiSuffixForForecastWeather() {
        return apiSuffixForForecastWeather;
    }

    public void setApiSuffixForForecastWeather(String apiSuffixForForecastWeather) {
        this.apiSuffixForForecastWeather = apiSuffixForForecastWeather;
    }

    public void setApiAppid(String apiAppid) {
        this.apiAppid = apiAppid;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiSuffixForCurrentWeather() {
        return apiSuffixForCurrentWeather;
    }

    public void setApiSuffixForCurrentWeather(String apiSuffixForCurrentWeather) {
        this.apiSuffixForCurrentWeather = apiSuffixForCurrentWeather;
    }

    public String getApiCallValidityMinuteForWeather() {
        return apiCallValidityMinuteForWeather;
    }

    public void setApiCallValidityMinuteForWeather(String apiCallValidityMinuteForWeather) {
        this.apiCallValidityMinuteForWeather = apiCallValidityMinuteForWeather;
    }

    public String getApiCallCountLimitPerMinuteForWeather() {
        return apiCallCountLimitPerMinuteForWeather;
    }

    public void setApiCallCountLimitPerMinuteForWeather(String apiCallCountLimitPerMinuteForWeather) {
        this.apiCallCountLimitPerMinuteForWeather = apiCallCountLimitPerMinuteForWeather;
    }
}
