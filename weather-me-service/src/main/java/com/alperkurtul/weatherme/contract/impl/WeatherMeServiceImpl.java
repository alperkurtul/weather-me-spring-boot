package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.configuration.WeatherMeConfigurationProperties;
import com.alperkurtul.weatherme.contract.WeatherMeService;
import com.alperkurtul.weatherme.contract.WeatherMeData;
import com.alperkurtul.weatherme.error.ErrorContants;
import com.alperkurtul.weatherme.error.exception.EntityNotFoundException;
import com.alperkurtul.weatherme.error.exception.MandatoryInputMissingException;
import com.alperkurtul.weatherme.error.handling.HttpExceptionDispatcher;
import com.alperkurtul.weatherme.mapper.ServiceMapper;
import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherId;
import com.alperkurtul.weatherme.model.WeatherMeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Service
public class WeatherMeServiceImpl implements WeatherMeService {

    @Autowired
    private WeatherMeConfigurationProperties weatherMeConfigurationProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherMeData weatherMeData;

    private ServiceMapper serviceMapper = ServiceMapper.INSTANCE;

    @Override
    public WeatherMeDto getCurrentWeather(WeatherMeDto var1) throws Exception {

        Weather weatherDataFromDb = null;
        String response = "";
        String requestUrl = "";
        String createOrUpdateDb = "";  // C : create , U : Update
        String dataExistInDb = "N";  // Y : exist in DB , N : doesnt exist in DB
        String createTimeExpired = "N";  // Y : expired  , N : doesnt expired

        // TODO : remove this code later
        if (var1.getLocationId() == null || var1.getLocationId().isEmpty()) {
            var1.setLocationId("745042");
        }



        if (var1.getLocationId() == null || var1.getLocationId().isEmpty()) {
            throw new MandatoryInputMissingException(null, ErrorContants.REASON_CODE_MANDATORY_INPUT_MISSING);
        }

        if (var1.getUnits() == null || var1.getUnits().isEmpty() ) {
            var1.setUnits("metric");
        }

        if (var1.getLanguage() == null || var1.getLanguage().isEmpty()) {
            var1.setLanguage("tr");
        }

        createOrUpdateDb = "";
        createTimeExpired = "N";
        Optional<Weather> optionalWeather = weatherMeData.findById(new WeatherId(var1.getLocationId(), var1.getLanguage(), var1.getUnits()));
        if (!optionalWeather.isPresent()) {
            dataExistInDb = "N";
            createOrUpdateDb = "C";
        } else {
            dataExistInDb = "Y";

            weatherDataFromDb = optionalWeather.get();
            if ( weatherDataFromDb.getCreateTime().plusHours(1).isBefore(LocalDateTime.now()) ) {
                createTimeExpired = "Y";
                createOrUpdateDb = "U";
            }
        }

        if ( dataExistInDb.equals("N") || ( dataExistInDb.equals("Y") && createTimeExpired.equals("Y") ) ) {
            if (var1.getLocationName() == null || var1.getLocationName().isEmpty()) {
                var1.setLocationName("Istanbul");
                // TODO : use findLocationNameByLocationId
            }

            //String requestUrl = "http://api.openweathermap.org/data/2.5/weather?q=Istanbul&lang=tr&units=metric&APPID=bcd5cca022de3d1a38619a0f353c5c77";
            String apiUrl = weatherMeConfigurationProperties.getOpenweathermapsiteApiUrl();
            String appId = weatherMeConfigurationProperties.getOpenweathermapsiteApiAppid();
            String currentweatherSuffix = weatherMeConfigurationProperties.getOpenweathermapsiteApiCurrentweatherSuffix();

            requestUrl = apiUrl + currentweatherSuffix +
                    "q=" + var1.getLocationName() +
                    "&lang=" + var1.getLanguage() +
                    "&units=" + var1.getUnits() +
                    "&appid=" + appId;
        } else {
            requestUrl = weatherDataFromDb.getRequestUrl();
            response = weatherDataFromDb.getWeatherJson();
            var1.setLocationName(weatherDataFromDb.getLocationName());
        }

        if ( dataExistInDb.equals("N") || ( dataExistInDb.equals("Y") && createTimeExpired.equals("Y") ) ) {
            if (weatherMeConfigurationProperties.getConnectToRealApi().equals("YES")) {
                try {
                    response = restTemplate.getForObject(requestUrl, String.class);
                } catch (HttpClientErrorException e) {
                    new HttpExceptionDispatcher().dispatchToException(e);
                }
            } else {
                response = "DUMMY";
            }
        }

        WeatherMeDto weatherMeDto = parseCurrentWeatherJson(response);
        weatherMeDto.setLanguage(var1.getLanguage());
        weatherMeDto.setUnits(var1.getUnits());

        if ( createOrUpdateDb.equals("C") || createOrUpdateDb.equals("U") ) {
            Weather weather = null;
            if ( createOrUpdateDb.equals("C") ) {
                weather = new Weather();
                WeatherId weatherId = new WeatherId(weatherMeDto.getLocationId(),
                        var1.getLanguage(),
                        var1.getUnits());
                weather.setWeatherId(weatherId);
                weather.setLocationName(var1.getLocationName());
                weather.setWeatherJson(response);
                weather.setRequestUrl(requestUrl);
            } else if ( createOrUpdateDb.equals("U") ) {
                weather = weatherDataFromDb;
                weather.setLocationName(var1.getLocationName());
                weather.setWeatherJson(response);
                weather.setRequestUrl(requestUrl);
                weather.setCreateTime(LocalDateTime.now());
            }

            weatherMeData.save(weather);
        }

        return weatherMeDto;
    }

    @Override
    public WeatherMeDto findById(WeatherMeDto var1) throws Exception {

        WeatherId weatherId = serviceMapper.toWeatherId(var1);

        Optional<Weather> optionalWeather = weatherMeData.findById(weatherId);

        if (!optionalWeather.isPresent()) {
            throw new EntityNotFoundException(null, ErrorContants.REASON_CODE_ENTITY_NOT_FOUND);
        }

        WeatherMeDto weatherMeDto = serviceMapper.toWeatherMeDto(optionalWeather.get(), optionalWeather.get().getWeatherId());

        return weatherMeDto;
    }

    @Override
    public String findLocationNameByLocationId(String locationId) throws Exception {
        return null;
    }

    @Override
    public String findLocationIdByLocationName(String locationId) throws Exception {
        return null;
    }

    private WeatherMeDto parseCurrentWeatherJson(String currentWeatherJson) {

        WeatherMeDto weatherMeDto;

        if (weatherMeConfigurationProperties.getConnectToRealApi().equals("YES")) {

            weatherMeDto = new WeatherMeDto();

            JsonParser jsonParser = JsonParserFactory.getJsonParser();
            Map<String, Object> mapJson = jsonParser.parseMap(currentWeatherJson);

            System.out.println("response: " + currentWeatherJson);
            String mapArray[] = new String[mapJson.size()];
            System.out.println("Items found: " + mapArray.length);

            for (Map.Entry<String, Object> entry : mapJson.entrySet()) {

                System.out.println(entry.toString() + " ## " + entry.getKey() + " ## " + entry.getValue());

                if (entry.getKey().equals("id")) {

                    weatherMeDto.setLocationId(entry.getValue().toString());

                } else if (entry.getKey().equals("name")) {

                    weatherMeDto.setLocationName(entry.getValue().toString());

                } else if (entry.getKey().equals("timezone")) {

                    weatherMeDto.setTimeZone(entry.getValue().toString());

                } else if (entry.getKey().equals("weather")) {

                    Map<String, Object> mapJson2 = (Map<String, Object>) ((ArrayList) entry.getValue()).get(0);
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("description")) {
                            weatherMeDto.setDescription(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("icon")) {
                            weatherMeDto.setDescriptionIcon("http://openweathermap.org/img/w/" + entry2.getValue().toString() + ".png");
                        }
                    }

                } else if (entry.getKey().equals("main")) {

                    Map<String, Object> mapJson2 = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("temp")) {
                            weatherMeDto.setRealTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("feels_like")) {
                            weatherMeDto.setFeelsTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("temp_min")) {
                            weatherMeDto.setMinTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("temp_max")) {
                            weatherMeDto.setMaxTemprature(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("pressure")) {
                            weatherMeDto.setPressure(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("humidity")) {
                            weatherMeDto.setHumidity(entry2.getValue().toString());
                        }
                    }

                } else if (entry.getKey().equals("sys")) {

                    Map<String, Object> mapJson2 = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                        if (entry2.getKey().equals("country")) {
                            weatherMeDto.setCountryCode(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("sunrise")) {
                            weatherMeDto.setSunRise(entry2.getValue().toString());
                        } else if (entry2.getKey().equals("sunset")) {
                            weatherMeDto.setSunSet(entry2.getValue().toString());
                        }
                    }

                }

            }

            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            long unixTime;
            String formattedDtm;

            unixTime = Long.valueOf(weatherMeDto.getSunRise()) + Long.valueOf(weatherMeDto.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            weatherMeDto.setSunRise(formattedDtm);

            unixTime = Long.valueOf(weatherMeDto.getSunSet()) + Long.valueOf(weatherMeDto.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            weatherMeDto.setSunSet(formattedDtm);

            unixTime = Long.valueOf(weatherMeDto.getTimeZone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            weatherMeDto.setTimeZone(formattedDtm);

        } else {
            weatherMeDto = new WeatherMeDto();
            weatherMeDto.setDescription("parçalı az bulutlu");
            weatherMeDto.setDescriptionIcon("http://openweathermap.org/img/w/03d.png");
            weatherMeDto.setRealTemprature("12.93");
            weatherMeDto.setFeelsTemprature("11.87");
            weatherMeDto.setMinTemprature("11.11");
            weatherMeDto.setMaxTemprature("15");
            weatherMeDto.setPressure("1012");
            weatherMeDto.setHumidity("67");
            weatherMeDto.setCountryCode("TR");
            weatherMeDto.setSunRise("09-03-2020 07:25:25");
            weatherMeDto.setSunSet("09-03-2020 19:03:55");
            weatherMeDto.setTimeZone("01-01-1970 03:00:00");
            weatherMeDto.setLocationId("745042");
            weatherMeDto.setLocationName("İstanbul");
        }

        return weatherMeDto;
    }

}
