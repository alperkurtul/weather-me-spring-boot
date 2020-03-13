package com.alperkurtul.weatherme.service.impl;

import com.alperkurtul.weatherme.configuration.WeatherMeConfigurationProperties;
import com.alperkurtul.weatherme.bean.CurrentWeatherDataBean;
import com.alperkurtul.weatherme.bean.WeatherRequestParametersBean;
import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherId;
import com.alperkurtul.weatherme.repository.WeatherRepository;
import com.alperkurtul.weatherme.service.GetDataFromOpenWeather;
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

    @Autowired
    private WeatherMeConfigurationProperties weatherMeConfigurationProperties;

    @Override
    public CurrentWeatherDataBean getCurrentWeather(WeatherRequestParametersBean weatherRequestParametersBean) {

        String response = "";
        CurrentWeatherDataBean currentWeatherDataBean;

        //String url = "http://api.openweathermap.org/data/2.5/weather?q=Istanbul&lang=tr&units=metric&APPID=bcd5cca022de3d1a38619a0f353c5c77";
        String url = weatherMeConfigurationProperties.getOpenweathermapsiteApiUrl();
        String appId = weatherMeConfigurationProperties.getOpenweathermapsiteApiAppid();
        String currentweatherSuffix = weatherMeConfigurationProperties.getOpenweathermapsiteApiCurrentweatherSuffix();
        url = url + currentweatherSuffix + "q=" + weatherRequestParametersBean.getLocationName() + "&lang=" + weatherRequestParametersBean.getLanguage() + "&units=" + weatherRequestParametersBean.getUnits() + "&APPID=" + appId;


        if (!weatherMeConfigurationProperties.getConnectToRealApi().equals("YES")) {

            currentWeatherDataBean = new CurrentWeatherDataBean();
            currentWeatherDataBean.setDescription("parçalı az bulutlu");
            currentWeatherDataBean.setDescriptionIcon("http://openweathermap.org/img/w/03d.png");
            currentWeatherDataBean.setRealTemprature("12.93");
            currentWeatherDataBean.setFeelsTemprature("11.87");
            currentWeatherDataBean.setMinTemprature("11.11");
            currentWeatherDataBean.setMaxTemprature("15");
            currentWeatherDataBean.setPressure("1012");
            currentWeatherDataBean.setHumidity("67");
            currentWeatherDataBean.setCountryCode("TR");
            currentWeatherDataBean.setSunRise("09-03-2020 07:25:25");
            currentWeatherDataBean.setSunSet("09-03-2020 19:03:55");
            currentWeatherDataBean.setTimeZone("01-01-1970 03:00:00");
            currentWeatherDataBean.setLocationId("745042");
            currentWeatherDataBean.setLocationName("İstanbul");

        } else {

            RestTemplate restTemplate = new RestTemplate();

            try {
                response = restTemplate.getForObject(url, String.class);
            } catch (HttpStatusCodeException e) {
                throw new RuntimeException();
            }

            currentWeatherDataBean = new CurrentWeatherDataBean();

            JsonParser jsonParser = JsonParserFactory.getJsonParser();
            Map<String, Object> mapJson = jsonParser.parseMap(response);

            System.out.println("response: " + response);
            String mapArray[] = new String[mapJson.size()];
            System.out.println("Items found: " + mapArray.length);

            for (Map.Entry<String, Object> entry : mapJson.entrySet()) {

                System.out.println(entry.toString() + " ## " + entry.getKey() + " ## " + entry.getValue());

                if (entry.getKey().equals("id")) {

                    currentWeatherDataBean.setLocationId(entry.getValue().toString());

                } else if (entry.getKey().equals("name")) {

                    currentWeatherDataBean.setLocationName(entry.getValue().toString());

                } else if (entry.getKey().equals("timezone")) {

                    currentWeatherDataBean.setTimeZone(entry.getValue().toString());

                } else if (entry.getKey().equals("weather")) {

                    Map<String,Object> mapJson2 = (Map<String, Object>) ((ArrayList)entry.getValue()).get(0);
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("description")) {
                            currentWeatherDataBean.setDescription(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("icon")) {
                            currentWeatherDataBean.setDescriptionIcon("http://openweathermap.org/img/w/" + entry2.getValue().toString() + ".png");
                        }
                    }

                } else if (entry.getKey().equals("main")) {

                    Map<String,Object> mapJson2 = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("temp")) {
                            currentWeatherDataBean.setRealTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("feels_like")) {
                            currentWeatherDataBean.setFeelsTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("temp_min")) {
                            currentWeatherDataBean.setMinTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("temp_max")) {
                            currentWeatherDataBean.setMaxTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("pressure")) {
                            currentWeatherDataBean.setPressure(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("humidity")) {
                            currentWeatherDataBean.setHumidity(entry2.getValue().toString());
                        }
                    }

                } else if (entry.getKey().equals("sys")) {

                    Map<String,Object> mapJson2 = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("country")) {
                            currentWeatherDataBean.setCountryCode(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("sunrise")) {
                            currentWeatherDataBean.setSunRise(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("sunset")) {
                            currentWeatherDataBean.setSunSet(entry2.getValue().toString());
                        }
                    }

                }

            }

            final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            long unixTime;
            String formattedDtm;

            unixTime = Long.valueOf(currentWeatherDataBean.getSunRise()) + Long.valueOf(currentWeatherDataBean.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            currentWeatherDataBean.setSunRise(formattedDtm);

            unixTime = Long.valueOf(currentWeatherDataBean.getSunSet()) + Long.valueOf(currentWeatherDataBean.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            currentWeatherDataBean.setSunSet(formattedDtm);

            unixTime = Long.valueOf(currentWeatherDataBean.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            currentWeatherDataBean.setTimeZone(formattedDtm);

        }

        Weather weather = new Weather();
        WeatherId weatherId = new WeatherId("111","aaa","bbb");
        weather.setWeatherId(weatherId);
        weather.setWeatherJson(response);
        weatherRepository.save(weather);

        return currentWeatherDataBean;
    }

}
