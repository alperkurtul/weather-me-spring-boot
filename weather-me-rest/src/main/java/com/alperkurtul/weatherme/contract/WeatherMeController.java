package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.model.CurrentWeatherResponse;
import com.alperkurtul.weatherme.model.LocationResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Validated
@RequestMapping(value = "/weatherme/v1")
@CrossOrigin("*")
public interface WeatherMeController {

    @GetMapping(value = "/getcurrentweather/{locationid}")
    CurrentWeatherResponse getCurrentWeather(@PathVariable("locationid") String locationId) throws Exception;

    @GetMapping(value = "/getlocationlist")
    LocationResponse getLocationList(@RequestParam("locationname") String locationName) throws Exception;

    @GetMapping(value = "/loadlocationstodb")
    Boolean loadLocationsToDb() throws Exception;

    @GetMapping(value = "/development-things")
    String developmentThings() throws Exception;

}
