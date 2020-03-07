package com.alperkurtul.weatherme.service;

import com.alperkurtul.weatherme.bean.WeatherDataBean;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.parser.Parser;
import java.util.Map;

@Service
public class GetDataFromOpenWeatherImpl implements GetDataFromOpenWeather {

    @Override
    public WeatherDataBean getCurrentWeather() {

        String url = "http://api.openweathermap.org/data/2.5/weather?q=Kadikoy&lang=tr&units=metric&APPID=bcd5cca022de3d1a38619a0f353c5c77";
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

        String mapArray[] = new String[mapJson.size()];
        System.out.println("Items found: " + mapArray.length);


        int startPoint;
        int endPoint;
        String toBeSearchedString;
        int i = 0;
        for (Map.Entry<String, Object> entry : mapJson.entrySet()) {

            startPoint = 0;
            endPoint = 0;
            toBeSearchedString = "description=";

            //weatherDataBean.setDescription( entry.toString().compareTo()  );
            System.out.println(entry.toString());


            startPoint = entry.toString().indexOf(toBeSearchedString);
            if (startPoint != -1) {
                endPoint = entry.toString().indexOf(",",startPoint);
                if (endPoint == -1) {
                    endPoint = entry.toString().indexOf("}",startPoint);
                }
                weatherDataBean.setDescription(entry.toString().substring(startPoint+toBeSearchedString.length(),endPoint));
                System.out.println("weatherDataBean.setDescription(): " + weatherDataBean.getDescription());

            }

            //System.out.println(entry.getKey() + " = " + entry.getValue());
            i++;
        }


        return weatherDataBean;
    }

}
