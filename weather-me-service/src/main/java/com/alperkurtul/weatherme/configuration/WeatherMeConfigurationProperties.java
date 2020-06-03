package com.alperkurtul.weatherme.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "weather.me")
public class WeatherMeConfigurationProperties {

    private String apiAppid;
    private String apiUrl;
    private String apiSuffixForWeather;
    private String apiCallValidityMinuteForWeather;
    private String apiCallCountLimitPerMinuteForWeather;

    public String getApiAppid() {
        return apiAppid;
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

    public String getApiSuffixForWeather() {
        return apiSuffixForWeather;
    }

    public void setApiSuffixForWeather(String apiSuffixForWeather) {
        this.apiSuffixForWeather = apiSuffixForWeather;
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
