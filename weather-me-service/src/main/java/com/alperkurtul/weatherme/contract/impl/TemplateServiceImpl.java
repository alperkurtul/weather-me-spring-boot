package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.configuration.TemplateConfigurationProperties;
import com.alperkurtul.weatherme.bean.TemplateResponse;
import com.alperkurtul.weatherme.bean.TemplateRequest;
import com.alperkurtul.weatherme.contract.TemplateService;
import com.alperkurtul.weatherme.data.TemplateData;
import com.alperkurtul.weatherme.data.model.Template;
import com.alperkurtul.weatherme.data.model.TemplateId;
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
public class TemplateServiceImpl<REQ, RES> implements TemplateService<REQ, RES> {

    @Autowired
    private TemplateConfigurationProperties templateConfigurationProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TemplateData templateData;

    @Override
    public RES getCurrentWeather(REQ var1) {

        String response = "";

        TemplateRequest templateRequest = (TemplateRequest) var1;

        //String requestUrl = "http://api.openweathermap.org/data/2.5/weather?q=Istanbul&lang=tr&units=metric&APPID=bcd5cca022de3d1a38619a0f353c5c77";
        String apiUrl = templateConfigurationProperties.getOpenweathermapsiteApiUrl();
        String appId = templateConfigurationProperties.getOpenweathermapsiteApiAppid();
        String currentweatherSuffix = templateConfigurationProperties.getOpenweathermapsiteApiCurrentweatherSuffix();

        String requestUrl = apiUrl + currentweatherSuffix +
                "q=" + templateRequest.getLocationName() +
                "&lang=" + templateRequest.getLanguage() +
                "&units=" + templateRequest.getUnits() +
                "&appid=" + appId;

        if (templateConfigurationProperties.getConnectToRealApi().equals("YES")) {
            try {
                response = restTemplate.getForObject(requestUrl, String.class);
            } catch (HttpStatusCodeException e) {
                throw new RuntimeException();
            }
        } else {
            response = "DUMMY";
        }

        TemplateResponse templateResponse = parseCurrentWeatherJson(response);

        Template template = new Template();
        TemplateId templateId = new TemplateId(templateResponse.getLocationId(),
                templateRequest.getLanguage(),
                templateRequest.getUnits());
        template.setTemplateId(templateId);
        template.setWeatherJson(response);
        template.setRequestUrl(requestUrl);

        templateData.saveTemplate(template);

        return (RES) templateResponse;
    }

    private TemplateResponse parseCurrentWeatherJson(String currentWeatherJson) {

        TemplateResponse templateResponse;

        if (templateConfigurationProperties.getConnectToRealApi().equals("YES")) {

            templateResponse = new TemplateResponse();

            JsonParser jsonParser = JsonParserFactory.getJsonParser();
            Map<String, Object> mapJson = jsonParser.parseMap(currentWeatherJson);

            System.out.println("response: " + currentWeatherJson);
            String mapArray[] = new String[mapJson.size()];
            System.out.println("Items found: " + mapArray.length);

            for (Map.Entry<String, Object> entry : mapJson.entrySet()) {

                System.out.println(entry.toString() + " ## " + entry.getKey() + " ## " + entry.getValue());

                if (entry.getKey().equals("id")) {

                    templateResponse.setLocationId(entry.getValue().toString());

                } else if (entry.getKey().equals("name")) {

                    templateResponse.setLocationName(entry.getValue().toString());

                } else if (entry.getKey().equals("timezone")) {

                    templateResponse.setTimeZone(entry.getValue().toString());

                } else if (entry.getKey().equals("weather")) {

                    Map<String, Object> mapJson2 = (Map<String, Object>) ((ArrayList) entry.getValue()).get(0);
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("description")) {
                            templateResponse.setDescription(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("icon")) {
                            templateResponse.setDescriptionIcon("http://openweathermap.org/img/w/" + entry2.getValue().toString() + ".png");
                        }
                    }

                } else if (entry.getKey().equals("main")) {

                    Map<String, Object> mapJson2 = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("temp")) {
                            templateResponse.setRealTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("feels_like")) {
                            templateResponse.setFeelsTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("temp_min")) {
                            templateResponse.setMinTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("temp_max")) {
                            templateResponse.setMaxTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("pressure")) {
                            templateResponse.setPressure(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("humidity")) {
                            templateResponse.setHumidity(entry2.getValue().toString());
                        }
                    }

                } else if (entry.getKey().equals("sys")) {

                    Map<String, Object> mapJson2 = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("country")) {
                            templateResponse.setCountryCode(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("sunrise")) {
                            templateResponse.setSunRise(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("sunset")) {
                            templateResponse.setSunSet(entry2.getValue().toString());
                        }
                    }

                }

            }

            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            long unixTime;
            String formattedDtm;

            unixTime = Long.valueOf(templateResponse.getSunRise()) + Long.valueOf(templateResponse.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            templateResponse.setSunRise(formattedDtm);

            unixTime = Long.valueOf(templateResponse.getSunSet()) + Long.valueOf(templateResponse.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            templateResponse.setSunSet(formattedDtm);

            unixTime = Long.valueOf(templateResponse.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            templateResponse.setTimeZone(formattedDtm);

        } else {
            templateResponse = new TemplateResponse();
            templateResponse.setDescription("parçalı az bulutlu");
            templateResponse.setDescriptionIcon("http://openweathermap.org/img/w/03d.png");
            templateResponse.setRealTemprature("12.93");
            templateResponse.setFeelsTemprature("11.87");
            templateResponse.setMinTemprature("11.11");
            templateResponse.setMaxTemprature("15");
            templateResponse.setPressure("1012");
            templateResponse.setHumidity("67");
            templateResponse.setCountryCode("TR");
            templateResponse.setSunRise("09-03-2020 07:25:25");
            templateResponse.setSunSet("09-03-2020 19:03:55");
            templateResponse.setTimeZone("01-01-1970 03:00:00");
            templateResponse.setLocationId("745042");
            templateResponse.setLocationName("İstanbul");
        }

        return templateResponse;
    }

}
