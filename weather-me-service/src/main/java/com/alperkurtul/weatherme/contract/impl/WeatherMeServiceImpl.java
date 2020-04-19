package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.configuration.WeatherMeConfigurationProperties;
import com.alperkurtul.weatherme.contract.LocationData;
import com.alperkurtul.weatherme.contract.WeatherMeService;
import com.alperkurtul.weatherme.contract.WeatherData;
import com.alperkurtul.weatherme.error.ErrorContants;
import com.alperkurtul.weatherme.error.exception.EntityNotFoundException;
import com.alperkurtul.weatherme.error.exception.MandatoryInputMissingException;
import com.alperkurtul.weatherme.error.handling.HttpExceptionDispatcher;
import com.alperkurtul.weatherme.json.currentweather.CurrentWeather;
import com.alperkurtul.weatherme.json.location.WeatherLocation;
import com.alperkurtul.weatherme.mapper.ServiceMapper;
import com.alperkurtul.weatherme.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherMeServiceImpl implements WeatherMeService {

    @Autowired
    private WeatherMeConfigurationProperties weatherMeConfigurationProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherData weatherData;

    @Autowired
    private LocationData locationData;

    private ServiceMapper serviceMapper = ServiceMapper.INSTANCE;

    private static Logger logger = LogManager.getLogger(WeatherMeServiceImpl.class);

    @Override
    public WeatherMeDto getCurrentWeather(WeatherMeDto var1) throws Exception {

        Weather weatherDataFromDb = null;
        String response = "";
        String requestUrl = "";
        String createOrUpdateDb = "";  // C : create , U : Update
        String dataExistInDb = "N";  // Y : exist in DB , N : doesnt exist in DB
        String createTimeExpired = "N";  // Y : expired , N : doesnt expired

        // TODO : remove this code later
        if (var1.getLocationId() == null || var1.getLocationId().isEmpty()) {
            var1.setLocationId("745044");
        }


        if (var1.getLocationId() == null || var1.getLocationId().isEmpty()) {
            throw new MandatoryInputMissingException(new Exception("locationId not valid : " + var1.getLocationId() == null ? "null" : var1.getLocationId() ),
                    ErrorContants.REASON_CODE_MANDATORY_INPUT_MISSING);
        }

        if (var1.getUnits() == null || var1.getUnits().isEmpty() ) {
            var1.setUnits("metric");
        }

        if (var1.getLanguage() == null || var1.getLanguage().isEmpty()) {
            var1.setLanguage("tr");
        }

        // check that location's weather info exists in DB
        createOrUpdateDb = "";
        createTimeExpired = "N";
        Optional<Weather> optionalWeather = weatherData.findById(new WeatherId(var1.getLocationId(), var1.getLanguage(), var1.getUnits()));
        if (!optionalWeather.isPresent()) {
            // location's weather info does not exist in DB
            dataExistInDb = "N";
            createOrUpdateDb = "C";
        } else {
            // location's weather info does exist in DB
            dataExistInDb = "Y";

            // check that location's weather info in DB has expired or not
            weatherDataFromDb = optionalWeather.get();
            if ( weatherDataFromDb.getCreateTime().plusHours(1).isBefore(LocalDateTime.now()) ) {
                // location's weather info in DB has expired
                createTimeExpired = "Y";
                createOrUpdateDb = "U";
            }
        }

        // check if we have to call the API again to get weather info
        if ( dataExistInDb.equals("N") || ( dataExistInDb.equals("Y") && createTimeExpired.equals("Y") ) ) {
            // we have to call the API again to get weather info
            if (var1.getLocationName() == null || var1.getLocationName().isEmpty()) {
                Optional<Location> optionalLocation = locationData.findById(Integer.valueOf(var1.getLocationId()));
                if (!optionalLocation.isPresent()) {
                    throw new EntityNotFoundException(new Exception("couldn't find locationId : " + var1.getLocationId()), ErrorContants.REASON_CODE_ENTITY_NOT_FOUND);
                }
                var1.setLocationName(optionalLocation.get().getLocationName());
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

            // Call the API to get new Weather info if 'ConnectToRealApi' parameter is 'YES'
            if (weatherMeConfigurationProperties.getConnectToRealApi().equals("YES")) {
                try {
                    response = restTemplate.getForObject(requestUrl, String.class);
                } catch (HttpClientErrorException e) {
                    new HttpExceptionDispatcher().dispatchToException(e);
                }
            } else {
                response = "DUMMY";
            }
        } else {
            // we dont need to call the API again. We can use the weather info from DB
            requestUrl = weatherDataFromDb.getRequestUrl();
            response = weatherDataFromDb.getWeatherJson();
            var1.setLocationName(weatherDataFromDb.getLocationName());
        }

        // Parse weather info JSON data
        WeatherMeDto weatherMeDtoOut = parseCurrentWeatherJson(response);
        weatherMeDtoOut.setLanguage(var1.getLanguage());
        weatherMeDtoOut.setUnits(var1.getUnits());

        // check if we have to create or update record to DB
        String createOrUpdateDbMain = "";
        Weather weather = null;
        if ( createOrUpdateDb.equals("C") || createOrUpdateDb.equals("U") ) {

            // Check if it is equal 'call API locationID' to 'response locationId', or not
            if (!var1.getLocationId().equals(weatherMeDtoOut.getLocationId())) {
                // 'call API locationId' is not equal to 'response locationId'
                createOrUpdateDbMain = createOrUpdateDb;
                Optional<Weather> optionalWeather2 = weatherData.findById(new WeatherId(weatherMeDtoOut.getLocationId(), var1.getLanguage(), var1.getUnits()));
                if (!optionalWeather2.isPresent()) {
                    createOrUpdateDb = "C";
                } else {
                    createOrUpdateDb = "U";
                    weatherDataFromDb = optionalWeather2.get();
                }
            }

            // 'response locationId' is being processed
            if ( createOrUpdateDb.equals("C") ) {
                weather = new Weather();
                WeatherId weatherId = new WeatherId(weatherMeDtoOut.getLocationId(),
                        var1.getLanguage(),
                        var1.getUnits());
                weather.setWeatherId(weatherId);
                if ( !var1.getLocationId().equals(weatherMeDtoOut.getLocationId()) ) {
                    Optional<Location> optionalLocation = locationData.findById(Integer.valueOf(weatherMeDtoOut.getLocationId()));
                    weather.setLocationName( optionalLocation.isPresent() ? optionalLocation.get().getLocationName() : "" );
                } else {
                    weather.setLocationName(var1.getLocationName());
                }
                weather.setWeatherJson(response);
                weather.setRequestUrl(requestUrl);
                weatherData.create(weather);
            } else if ( createOrUpdateDb.equals("U") ) {
                weather = weatherDataFromDb;
                if ( !var1.getLocationId().equals(weatherMeDtoOut.getLocationId()) ) {
                    Optional<Location> optionalLocation = locationData.findById(Integer.valueOf(weatherMeDtoOut.getLocationId()));
                    weather.setLocationName( optionalLocation.isPresent() ? optionalLocation.get().getLocationName() : "" );
                } else {
                    weather.setLocationName(var1.getLocationName());
                }
                weather.setWeatherJson(response);
                weather.setRequestUrl(requestUrl);
                weather.setCreateTime(LocalDateTime.now());
                weatherData.update(weather);
            }

            // 'call API locationId' is being processed
            if (!var1.getLocationId().equals(weatherMeDtoOut.getLocationId())) {
                Weather weather2 = new Weather();
                WeatherId weatherId = new WeatherId(var1.getLocationId(),
                        var1.getLanguage(),
                        var1.getUnits());
                weather2.setWeatherId(weatherId);
                weather2.setLocationName(var1.getLocationName());
                weather2.setWeatherJson(response);
                weather2.setRequestUrl(requestUrl);
                if (createOrUpdateDbMain.equals("C")) {
                    weatherData.create(weather2);
                } else if (createOrUpdateDbMain.equals("U")) {
                    weather2.setCreateTime(LocalDateTime.now());
                    weatherData.update(weather2);
                }
            }

        }

        return weatherMeDtoOut;

    }

    @Override
    public WeatherMeDto findById(WeatherMeDto var1) throws Exception {

        WeatherId weatherId = serviceMapper.toWeatherId(var1);

        Optional<Weather> optionalWeather = weatherData.findById(weatherId);

        if (!optionalWeather.isPresent()) {
            throw new EntityNotFoundException(new Exception("Error in Weather model"), ErrorContants.REASON_CODE_ENTITY_NOT_FOUND);
        }

        WeatherMeDto weatherMeDto = serviceMapper.toWeatherMeDto(optionalWeather.get(), optionalWeather.get().getWeatherId());

        return weatherMeDto;
    }

    @Override
    public List<LocationDto> findAllLocationByLocationName(String locationName, String language) throws Exception {

        List<Location> locationList = locationData.findAllLocationByLocationName(locationName, language);

        List<LocationDto> locationDtoList = new ArrayList<>();
        for (Location location : locationList) {
            LocationDto locationDto = serviceMapper.toLocationDto(location);
            locationDtoList.add(locationDto);
        }

        return locationDtoList;
    }

    @Override
    public Boolean loadLocationsToDb() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        WeatherLocation[] locations = objectMapper.readValue(new File("/Users/alperkurtul/Desktop/city.list.json"), WeatherLocation[].class);

        logger.info("locations.length: " + locations.length);

        int saved = 0;
        int savedInThisIteration = 0;
        List<Location> locationsForIterable = new ArrayList<>();
        for (WeatherLocation loc : locations) {
            Location location = new Location();
            location.setLocationId(Integer.valueOf(loc.getId()));
            location.setLocationName(loc.getName());
            location.setCountry(loc.getCountry());
            location.setState(loc.getState());
            location.setLongitude(loc.getCoord().getLon());
            location.setLatitude(loc.getCoord().getLat());

            Locale locale = Locale.forLanguageTag(loc.getCountry());
            location.setLowerCaseLocationName(loc.getName().toLowerCase(locale));

            locationsForIterable.add(location);

            saved ++;
            savedInThisIteration ++;
            if ( ( saved % 20000 == 0 ) || ( saved == locations.length ) ) {
                logger.info("Saving - locations.length: " + locations.length + "  / saving now    : " + savedInThisIteration + " records");
                locationData.iterableCreate(locationsForIterable);
                logger.info("SAVED  - locations.length: " + locations.length + "  / totally saved : " + saved + " records");
                locationsForIterable.removeAll(locationsForIterable);
                savedInThisIteration = 0;
            }

        }

        return true;

    }

    private WeatherMeDto parseCurrentWeatherJson(String currentWeatherJson) throws Exception {

        WeatherMeDto weatherMeDto;

        if (weatherMeConfigurationProperties.getConnectToRealApi().equals("YES")) {

            ObjectMapper objectMapper = new ObjectMapper();

            CurrentWeather currentWeather = objectMapper.readValue(currentWeatherJson, CurrentWeather.class);

            weatherMeDto = new WeatherMeDto();
            weatherMeDto.setLocationId(currentWeather.getId());
            weatherMeDto.setLocationName(currentWeather.getName());
            weatherMeDto.setTimeZone(currentWeather.getTimezone());
            weatherMeDto.setDescription(currentWeather.getWeather()[0].getDescription());
            weatherMeDto.setDescriptionIcon("http://openweathermap.org/img/w/" + currentWeather.getWeather()[0].getIcon() + ".png");
            weatherMeDto.setRealTemprature(currentWeather.getMain().getTemp());
            weatherMeDto.setFeelsTemprature(currentWeather.getMain().getFeels_like());
            weatherMeDto.setMinTemprature(currentWeather.getMain().getTemp_min());
            weatherMeDto.setMaxTemprature(currentWeather.getMain().getTemp_max());
            weatherMeDto.setPressure(currentWeather.getMain().getPressure());
            weatherMeDto.setHumidity(currentWeather.getMain().getHumidity());
            weatherMeDto.setCountryCode(currentWeather.getSys().getCountry());
            weatherMeDto.setSunRise(currentWeather.getSys().getSunrise());
            weatherMeDto.setSunSet(currentWeather.getSys().getSunset());

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
            weatherMeDto.setLocationName("Istanbul");
        }

        return weatherMeDto;
    }

}
