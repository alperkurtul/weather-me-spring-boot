package com.alperkurtul.weatherme.configuration;

import com.alperkurtul.weatherme.contract.WeatherMeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix="weather.me.locations", value="load")
public class WeatherMeServiceConfiguration {

    @Autowired
    WeatherMeService weatherMeService;

    @Bean
    public void loadLocations() throws Exception {
        weatherMeService.loadLocationsToDb();
    }

}
