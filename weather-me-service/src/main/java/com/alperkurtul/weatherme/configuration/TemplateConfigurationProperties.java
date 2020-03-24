package com.alperkurtul.weatherme.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "weather.me")
public class TemplateConfigurationProperties {

    private String connectToRealApi;
    private String openweathermapsiteApiAppid;
    private String openweathermapsiteApiUrl;
    private String openweathermapsiteApiCurrentweatherSuffix;

    public String getConnectToRealApi() {
        return connectToRealApi;
    }

    public void setConnectToRealApi(String connectToRealApi) {
        this.connectToRealApi = connectToRealApi;
    }

    public String getOpenweathermapsiteApiAppid() {
        return openweathermapsiteApiAppid;
    }

    public void setOpenweathermapsiteApiAppid(String openweathermapsiteApiAppid) {
        this.openweathermapsiteApiAppid = openweathermapsiteApiAppid;
    }

    public String getOpenweathermapsiteApiUrl() {
        return openweathermapsiteApiUrl;
    }

    public void setOpenweathermapsiteApiUrl(String openweathermapsiteApiUrl) {
        this.openweathermapsiteApiUrl = openweathermapsiteApiUrl;
    }

    public String getOpenweathermapsiteApiCurrentweatherSuffix() {
        return openweathermapsiteApiCurrentweatherSuffix;
    }

    public void setOpenweathermapsiteApiCurrentweatherSuffix(String openweathermapsiteApiCurrentweatherSuffix) {
        this.openweathermapsiteApiCurrentweatherSuffix = openweathermapsiteApiCurrentweatherSuffix;
    }
}
