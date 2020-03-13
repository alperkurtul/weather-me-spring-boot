package com.alperkurtul.weatherme.service;

import com.alperkurtul.weatherme.bean.WeatherDataBean;
import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherId;
import com.alperkurtul.weatherme.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@Service
public class GetDataFromOpenWeatherImpl implements GetDataFromOpenWeather {

    @Autowired
    private WeatherRepository weatherRepository;

    @Override
    public WeatherDataBean getCurrentWeather() {

        String url = "http://api.openweathermap.org/data/2.5/weather?q=Istanbul&lang=tr&units=metric&APPID=bcd5cca022de3d1a38619a0f353c5c77";
        String response;
        RestTemplate restTemplate = new RestTemplate();

        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException();
        }


        WeatherDataBean weatherDataBean = new WeatherDataBean();

        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        Map<String, Object> mapJson = jsonParser.parseMap(response);

        System.out.println("response: " + response);
        String mapArray[] = new String[mapJson.size()];
        System.out.println("Items found: " + mapArray.length);

        for (Map.Entry<String, Object> entry : mapJson.entrySet()) {

            System.out.println(entry.toString() + " ## " + entry.getKey() + " ## " + entry.getValue());

            if (entry.getKey().equals("id")) {

                weatherDataBean.setLocationId(entry.getValue().toString());

            } else if (entry.getKey().equals("name")) {

                weatherDataBean.setLocationName(entry.getValue().toString());

            } else if (entry.getKey().equals("timezone")) {

                weatherDataBean.setTimeZone(entry.getValue().toString());

            } else if (entry.getKey().equals("weather")) {

                Map<String,Object> mapJson2 = (Map<String, Object>) ((ArrayList)entry.getValue()).get(0);
                for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                    if (entry2.getKey().equals("description")) {
                        weatherDataBean.setDescription(entry2.getValue().toString());
                    } else if (entry2.getKey().equals("icon")) {
                        weatherDataBean.setDescriptionIcon("http://openweathermap.org/img/w/" + entry2.getValue().toString() + ".png");
                    }
                }

            } else if (entry.getKey().equals("main")) {

                Map<String,Object> mapJson2 = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                    if (entry2.getKey().equals("temp")) {
                        weatherDataBean.setRealTemprature(entry2.getValue().toString());
                    } else if (entry2.getKey().equals("feels_like")) {
                        weatherDataBean.setFeelsTemprature(entry2.getValue().toString());
                    } else if (entry2.getKey().equals("temp_min")) {
                        weatherDataBean.setMinTemprature(entry2.getValue().toString());
                    } else if (entry2.getKey().equals("temp_max")) {
                        weatherDataBean.setMaxTemprature(entry2.getValue().toString());
                    } else if (entry2.getKey().equals("pressure")) {
                        weatherDataBean.setPressure(entry2.getValue().toString());
                    } else if (entry2.getKey().equals("humidity")) {
                        weatherDataBean.setHumidity(entry2.getValue().toString());
                    }
                }

            } else if (entry.getKey().equals("sys")) {

                Map<String,Object> mapJson2 = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                    if (entry2.getKey().equals("country")) {
                        weatherDataBean.setCountryCode(entry2.getValue().toString());
                    } else if (entry2.getKey().equals("sunrise")) {
                        weatherDataBean.setSunRise(entry2.getValue().toString());
                    } else if (entry2.getKey().equals("sunset")) {
                        weatherDataBean.setSunSet(entry2.getValue().toString());
                    }
                }

            }

        }

        final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        long unixTime;
        String formattedDtm;

        unixTime = Long.valueOf(weatherDataBean.getSunRise()) + Long.valueOf(weatherDataBean.getTimeZone());
        formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
        weatherDataBean.setSunRise(formattedDtm);

        unixTime = Long.valueOf(weatherDataBean.getSunSet()) + Long.valueOf(weatherDataBean.getTimeZone());
        formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
        weatherDataBean.setSunSet(formattedDtm);

        unixTime = Long.valueOf(weatherDataBean.getTimeZone());
        formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
        weatherDataBean.setTimeZone(formattedDtm);

        /*Weather weather = new Weather();
        WeatherId weatherId = new WeatherId("111","aaa","bbb");
        weather.setWeatherJson(response);
        weatherRepository.save(weather);*/

        return weatherDataBean;
    }

}
