package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.bean.CurrentWeatherResponse;
import com.alperkurtul.weatherme.bean.WeatherMeRequest;
import org.springframework.web.bind.annotation.*;

//@Validated
@RequestMapping(value = "/sample/v1")
@CrossOrigin("*")
public interface WeatherMeController {

    @GetMapping(value = "/getreq/{key}")
    CurrentWeatherResponse getTemplate(@PathVariable("key") String key) throws Exception;

    @PostMapping(value = "/putreq")
    CurrentWeatherResponse setTemplate(@RequestBody WeatherMeRequest request) throws Exception;

}
