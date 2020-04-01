package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.configuration.WeatherMeConfigurationProperties;
import com.alperkurtul.weatherme.bean.CurrentWeatherResponse;
import com.alperkurtul.weatherme.bean.WeatherMeRequest;
import com.alperkurtul.weatherme.contract.WeatherMeService;
import com.alperkurtul.weatherme.data.WeatherMeData;
import com.alperkurtul.weatherme.data.error.handling.HttpExceptionDispatcher;
import com.alperkurtul.weatherme.data.model.Weather;
import com.alperkurtul.weatherme.data.model.WeatherId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@Service
public class WeatherMeServiceImpl<REQ, RES> implements WeatherMeService<REQ, RES> {

    @Autowired
    private WeatherMeConfigurationProperties weatherMeConfigurationProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherMeData weatherMeData;

    @Override
    public RES getCurrentWeather(REQ var1) {

        String response = "";

        WeatherMeRequest weatherMeRequest = (WeatherMeRequest) var1;

        //String requestUrl = "http://api.openweathermap.org/data/2.5/weather?q=Istanbul&lang=tr&units=metric&APPID=bcd5cca022de3d1a38619a0f353c5c77";
        String apiUrl = weatherMeConfigurationProperties.getOpenweathermapsiteApiUrl();
        String appId = weatherMeConfigurationProperties.getOpenweathermapsiteApiAppid();
        String currentweatherSuffix = weatherMeConfigurationProperties.getOpenweathermapsiteApiCurrentweatherSuffix();

        String requestUrl = apiUrl + currentweatherSuffix +
                "q=" + weatherMeRequest.getLocationName() +
                "&lang=" + weatherMeRequest.getLanguage() +
                "&units=" + weatherMeRequest.getUnits() +
                "&appid=" + appId;

        if (weatherMeConfigurationProperties.getConnectToRealApi().equals("YES")) {
            try {
                response = restTemplate.getForObject(requestUrl, String.class);
            } catch (HttpClientErrorException e) {
                new HttpExceptionDispatcher().dispatchToException(e);
            }
        } else {
            response = "DUMMY";
        }

        CurrentWeatherResponse currentWeatherResponse = parseCurrentWeatherJson(response);

        Weather weather = new Weather();
        WeatherId weatherId = new WeatherId(currentWeatherResponse.getLocationId(),
                weatherMeRequest.getLanguage(),
                weatherMeRequest.getUnits());
        weather.setWeatherId(weatherId);
        weather.setWeatherJson(response);
        weather.setRequestUrl(requestUrl);

        weatherMeData.saveTemplate(weather);

        return (RES) currentWeatherResponse;
    }

    private CurrentWeatherResponse parseCurrentWeatherJson(String currentWeatherJson) {

        CurrentWeatherResponse currentWeatherResponse;

        if (weatherMeConfigurationProperties.getConnectToRealApi().equals("YES")) {

            currentWeatherResponse = new CurrentWeatherResponse();

            JsonParser jsonParser = JsonParserFactory.getJsonParser();
            Map<String, Object> mapJson = jsonParser.parseMap(currentWeatherJson);

            System.out.println("response: " + currentWeatherJson);
            String mapArray[] = new String[mapJson.size()];
            System.out.println("Items found: " + mapArray.length);

            for (Map.Entry<String, Object> entry : mapJson.entrySet()) {

                System.out.println(entry.toString() + " ## " + entry.getKey() + " ## " + entry.getValue());

                if (entry.getKey().equals("id")) {

                    currentWeatherResponse.setLocationId(entry.getValue().toString());

                } else if (entry.getKey().equals("name")) {

                    currentWeatherResponse.setLocationName(entry.getValue().toString());

                } else if (entry.getKey().equals("timezone")) {

                    currentWeatherResponse.setTimeZone(entry.getValue().toString());

                } else if (entry.getKey().equals("weather")) {

                    Map<String, Object> mapJson2 = (Map<String, Object>) ((ArrayList) entry.getValue()).get(0);
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("description")) {
                            currentWeatherResponse.setDescription(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("icon")) {
                            currentWeatherResponse.setDescriptionIcon("http://openweathermap.org/img/w/" + entry2.getValue().toString() + ".png");
                        }
                    }

                } else if (entry.getKey().equals("main")) {

                    Map<String, Object> mapJson2 = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("temp")) {
                            currentWeatherResponse.setRealTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("feels_like")) {
                            currentWeatherResponse.setFeelsTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("temp_min")) {
                            currentWeatherResponse.setMinTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("temp_max")) {
                            currentWeatherResponse.setMaxTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("pressure")) {
                            currentWeatherResponse.setPressure(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("humidity")) {
                            currentWeatherResponse.setHumidity(entry2.getValue().toString());
                        }
                    }

                } else if (entry.getKey().equals("sys")) {

                    Map<String, Object> mapJson2 = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("country")) {
                            currentWeatherResponse.setCountryCode(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("sunrise")) {
                            currentWeatherResponse.setSunRise(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("sunset")) {
                            currentWeatherResponse.setSunSet(entry2.getValue().toString());
                        }
                    }

                }

            }

            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            long unixTime;
            String formattedDtm;

            unixTime = Long.valueOf(currentWeatherResponse.getSunRise()) + Long.valueOf(currentWeatherResponse.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            currentWeatherResponse.setSunRise(formattedDtm);

            unixTime = Long.valueOf(currentWeatherResponse.getSunSet()) + Long.valueOf(currentWeatherResponse.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            currentWeatherResponse.setSunSet(formattedDtm);

            unixTime = Long.valueOf(currentWeatherResponse.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            currentWeatherResponse.setTimeZone(formattedDtm);

        } else {
            currentWeatherResponse = new CurrentWeatherResponse();
            currentWeatherResponse.setDescription("parçalı az bulutlu");
            currentWeatherResponse.setDescriptionIcon("http://openweathermap.org/img/w/03d.png");
            currentWeatherResponse.setRealTemprature("12.93");
            currentWeatherResponse.setFeelsTemprature("11.87");
            currentWeatherResponse.setMinTemprature("11.11");
            currentWeatherResponse.setMaxTemprature("15");
            currentWeatherResponse.setPressure("1012");
            currentWeatherResponse.setHumidity("67");
            currentWeatherResponse.setCountryCode("TR");
            currentWeatherResponse.setSunRise("09-03-2020 07:25:25");
            currentWeatherResponse.setSunSet("09-03-2020 19:03:55");
            currentWeatherResponse.setTimeZone("01-01-1970 03:00:00");
            currentWeatherResponse.setLocationId("745042");
            currentWeatherResponse.setLocationName("İstanbul");
        }

        return currentWeatherResponse;
    }

}
