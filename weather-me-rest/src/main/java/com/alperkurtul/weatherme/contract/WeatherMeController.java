package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.CurrentWeatherResponse;
import com.alperkurtul.weatherme.model.WeatherMeRequest;
import org.springframework.web.bind.annotation.*;

//@Validated
@RequestMapping(value = "/weatherme/v1")
@CrossOrigin("*")
public interface WeatherMeController {

    @GetMapping(value = "/getcurrentweather/{locationid}")
    CurrentWeatherResponse getCurrentWeather(@PathVariable("locationid") String locationId) throws Exception;

    @PostMapping(value = "/findById")
    CurrentWeatherResponse findById(@RequestBody WeatherMeRequest request) throws Exception;

}
