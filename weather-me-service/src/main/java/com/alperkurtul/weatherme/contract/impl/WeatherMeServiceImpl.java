package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.configuration.WeatherMeConfigurationProperties;
import com.alperkurtul.weatherme.contract.LocationData;
import com.alperkurtul.weatherme.contract.WeatherHistoryData;
import com.alperkurtul.weatherme.contract.WeatherMeService;
import com.alperkurtul.weatherme.contract.WeatherData;
import com.alperkurtul.weatherme.error.ErrorContants;
import com.alperkurtul.weatherme.error.exception.EntityNotFoundExceptionN10;
import com.alperkurtul.weatherme.error.exception.MandatoryInputMissingExceptionN20;
import com.alperkurtul.weatherme.error.handling.HttpExceptionDispatcher;
import com.alperkurtul.weatherme.json.currentweather.CurrentWeather;
import com.alperkurtul.weatherme.json.location.WeatherLocation;
import com.alperkurtul.weatherme.json.threehourforecastfivedays.ForecastInfo;
import com.alperkurtul.weatherme.json.threehourforecastfivedays.ThreeHourForecastFiveDays;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherMeServiceImpl implements WeatherMeService {

    private String weatherRequestUrl = "";
    private String forecastRequestUrl = "";

    @Autowired
    private WeatherMeConfigurationProperties weatherMeConfigurationProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherData weatherData;

    @Autowired
    private WeatherHistoryData weatherHistoryData;

    @Autowired
    private LocationData locationData;

    private ServiceMapper serviceMapper = ServiceMapper.INSTANCE;

    private static Logger logger = LogManager.getLogger(WeatherMeServiceImpl.class);

    @Override
    public WeatherMeDto getCurrentWeather(WeatherMeDto var1) throws Exception {

        logger.info("'WeatherMeServiceImpl.getCurrentWeather' running...");

        Weather weatherDataFromDb = null;
        Weather weatherDataFromDbMain = null;
        String weatherResponse = "";
        String forecastResponse = "";
        String createOrUpdateDb = ""; // C : create , U : Update
        String dataExistInDb = "N"; // Y : exist in DB , N : does not exist in DB
        String updateTimeExpired = "N"; // Y : expired , N : does not expired

        // TODO : remove this code later
        if (var1.getLocationId() == null || var1.getLocationId().isEmpty()) {
            var1.setLocationId("745044");
        }

        if (var1.getLocationId() == null || var1.getLocationId().isEmpty()) {
            throw new MandatoryInputMissingExceptionN20(
                    new Exception(
                            "locationId not valid : " + var1.getLocationId() == null ? "null" : var1.getLocationId()),
                    ErrorContants.REASON_CODE_MANDATORY_INPUT_MISSING_LOCATIONID);
        }

        if (var1.getUnits() == null || var1.getUnits().isEmpty()) {
            var1.setUnits("metric");
        }

        if (var1.getLanguage() == null || var1.getLanguage().isEmpty()) {
            var1.setLanguage("tr");
        }

        logger.info("locationId: " + var1.getLocationId() + " units: " + var1.getUnits() + " language: "
                + var1.getLanguage());

        // check that location's weather info exists in DB
        createOrUpdateDb = "";
        updateTimeExpired = "N";
        Optional<Weather> optionalWeather = weatherData
                .findById(new WeatherId(Integer.valueOf(var1.getLocationId()), var1.getLanguage(), var1.getUnits()));
        if (!optionalWeather.isPresent()) {
            // location's weather info does not exist in DB
            logger.info("location's weather info does not exist in DB !!");
            dataExistInDb = "N";
            createOrUpdateDb = "C";
        } else {
            // location's weather info does exist in DB
            logger.info("location's weather info does exist in DB !!");
            dataExistInDb = "Y";

            // check that location's weather info in DB has expired or not
            logger.info("checking location's weather info in DB has expired or not !!");
            weatherDataFromDb = optionalWeather.get();
            logger.info("weatherMeConfigurationProperties.getApiCallValidityMinuteForWeather() : "
                    + weatherMeConfigurationProperties.getApiCallValidityMinuteForWeather());
            logger.info("weatherDataFromDb.getUpdateTime() : " + weatherDataFromDb.getUpdateTime());
            if (weatherDataFromDb.getUpdateTime()
                    .plusMinutes(Integer.valueOf(weatherMeConfigurationProperties.getApiCallValidityMinuteForWeather()))
                    .isBefore(LocalDateTime.now())) {
                // if ( true ) { // TODO : This line is for testing. Remove this line while
                // deployment
                // location's weather info in DB has expired
                logger.info("location's weather info in DB has expired !!");
                updateTimeExpired = "Y";
                createOrUpdateDb = "U";
            } else {
                logger.info("location's weather info in DB has not expired !!");
            }
        }

        // check if we have to call the API again to get weather info
        if (dataExistInDb.equals("N") || (dataExistInDb.equals("Y") && updateTimeExpired.equals("Y"))) {
            // it may be needed to call the API again to get weather info
            logger.info("it may be needed to call the API again to get weather info !!");

            // check if apiCallCountLimitPerMinute has Exceeded or not
            logger.info("check if apiCallCountLimitPerMinute has Exceeded or not !!");
            Long apiCallCountLimitPerMinute = weatherHistoryData
                    .calculateCallCountSinceGivenHistoryCreateTime(LocalDateTime.now().minusMinutes(1));
            logger.info("weatherMeConfigurationProperties.getApiCallCountLimitPerMinuteForWeather() : "
                    + weatherMeConfigurationProperties.getApiCallCountLimitPerMinuteForWeather());
            logger.info("* ******************************************************************************* *");
            logger.info("Occurence Count of apiCallCountLimitPerMinute : " + apiCallCountLimitPerMinute);
            logger.info("* ******************************************************************************* *");
            if (apiCallCountLimitPerMinute >= Long
                    .valueOf(weatherMeConfigurationProperties.getApiCallCountLimitPerMinuteForWeather())) {
                // apiCallCountLimitPerMinute has Exceeded
                logger.error("apiCallCountLimitPerMinute has Exceeded !!");
                throw new EntityNotFoundExceptionN10(
                        new Exception("apiCallCountLimitPerMinute has Exceeded. Max Count : "
                                + weatherMeConfigurationProperties.getApiCallCountLimitPerMinuteForWeather()),
                        ErrorContants.REASON_CODE_API_CALL_COUNT_EXCEEDED);
            } else {
                // we have to call the API again to get weather info
                logger.info("we have to call the API again to get weather info !!");
                if (var1.getLocationName() == null || var1.getLocationName().isEmpty()) {
                    Optional<Location> optionalLocation = locationData.findById(Integer.valueOf(var1.getLocationId()));
                    if (!optionalLocation.isPresent()) {
                        throw new EntityNotFoundExceptionN10(
                                new Exception("couldn't find locationId : " + var1.getLocationId()),
                                ErrorContants.REASON_CODE_ENTITY_NOT_FOUND);
                    }
                    var1.setLocationName(optionalLocation.get().getLocationName());
                }

                weatherResponse = callCurrentWeatherApi(var1);
                forecastResponse = callForecastWeatherApi(var1);
            }
        } else {
            // we dont need to call the API again. We can use the weather info from DB
            logger.info("we dont need to call the API again. We can use the weather info from DB !!");
            weatherRequestUrl = weatherDataFromDb.getWeatherRequest();
            forecastRequestUrl = weatherDataFromDb.getForecastRequest();
            weatherResponse = weatherDataFromDb.getWeatherResponse();
            forecastResponse = weatherDataFromDb.getForecastResponse();
            var1.setLocationName(weatherDataFromDb.getLocationName());
        }

        // Parse weather info JSON data
        WeatherMeDto weatherMeDtoOut = parseCurrentWeatherJson(weatherResponse);
        weatherMeDtoOut.setLanguage(var1.getLanguage());
        weatherMeDtoOut.setUnits(var1.getUnits());

        // Parse forecast info JSON data
        ArrayList<WeatherNearFuture> weatherNearFutures = parseForecastWeatherJson(forecastResponse);
        weatherMeDtoOut.setNearFuture(weatherNearFutures);

        // Parse forecast info JSON data for Next Days
        ArrayList<WeatherNextDay> weatherNextDays = parseForecastWeatherJsonForNextDays(forecastResponse);
        weatherMeDtoOut.setNextDays(weatherNextDays);

        // check if we have to create or update record to DB
        String createOrUpdateDbMain = "";
        Weather weather = null;
        boolean apiCalledFlag = true;
        if (createOrUpdateDb.equals("C") || createOrUpdateDb.equals("U")) {

            // Check if it is equal 'call API locationID' to 'response locationId', or not
            if (!var1.getLocationId().equals(weatherMeDtoOut.getLocationId())) {
                // 'call API locationId' is not equal to 'response locationId'
                logger.info("'call API locationId' is not equal to 'response locationId'");
                logger.info("Two records will be Created or Updated in Weather table !!!");
                apiCalledFlag = false;
                createOrUpdateDbMain = createOrUpdateDb;
                weatherDataFromDbMain = weatherDataFromDb;
                Optional<Weather> optionalWeather2 = weatherData.findById(new WeatherId(
                        Integer.valueOf(weatherMeDtoOut.getLocationId()), var1.getLanguage(), var1.getUnits()));
                if (!optionalWeather2.isPresent()) {
                    createOrUpdateDb = "C";
                } else {
                    createOrUpdateDb = "U";
                    weatherDataFromDb = optionalWeather2.get();
                }
            } else {
                logger.info("'call API locationId' is equal to 'response locationId'");
                logger.info("Only One record will be Created or Updated in Weather table !!!");
            }

            // 'response locationId' is being processed
            logger.info("'response locationId' is being processed !!!");
            if (createOrUpdateDb.equals("C") || createOrUpdateDb.equals("U")) {
                if (createOrUpdateDb.equals("C")) {
                    logger.info("'response API locationId: '" + weatherMeDtoOut.getLocationId() + " is being Created");
                    weather = new Weather();
                    WeatherId weatherId = new WeatherId(Integer.valueOf(weatherMeDtoOut.getLocationId()),
                            var1.getLanguage(), var1.getUnits());
                    weather.setWeatherId(weatherId);
                    if (!var1.getLocationId().equals(weatherMeDtoOut.getLocationId())) {
                        Optional<Location> optionalLocation = locationData
                                .findById(Integer.valueOf(weatherMeDtoOut.getLocationId()));
                        weather.setLocationName(
                                optionalLocation.isPresent() ? optionalLocation.get().getLocationName() : "");
                    } else {
                        weather.setLocationName(var1.getLocationName());
                    }
                    weather.setWeatherRequest(weatherRequestUrl);
                    weather.setWeatherResponse(weatherResponse);
                    weather.setForecastRequest(forecastRequestUrl);
                    weather.setForecastResponse(forecastResponse);
                } else if (createOrUpdateDb.equals("U")) {
                    logger.info("'response API locationId: '" + weatherMeDtoOut.getLocationId() + " is being Updated");
                    weather = weatherDataFromDb;
                    if (!var1.getLocationId().equals(weatherMeDtoOut.getLocationId())) {
                        Optional<Location> optionalLocation = locationData
                                .findById(Integer.valueOf(weatherMeDtoOut.getLocationId()));
                        weather.setLocationName(
                                optionalLocation.isPresent() ? optionalLocation.get().getLocationName() : "");
                    } else {
                        weather.setLocationName(var1.getLocationName());
                    }
                    weather.setWeatherRequest(weatherRequestUrl);
                    weather.setWeatherResponse(weatherResponse);
                    weather.setForecastRequest(forecastRequestUrl);
                    weather.setForecastResponse(forecastResponse);
                    weather.setUpdateTime(LocalDateTime.now());
                }
                weather.setApiCalledFlag(apiCalledFlag);
                createOrUpdateWeather(weather, createOrUpdateDb);
            }

            // 'call API locationId' is being processed
            logger.info("'call API locationId' is being processed !!!");
            if (!var1.getLocationId().equals(weatherMeDtoOut.getLocationId())) {
                logger.info("'call API locationId' is not equal to 'response locationId'");
                Weather weather2 = new Weather();
                WeatherId weatherId = new WeatherId(Integer.valueOf(var1.getLocationId()), var1.getLanguage(),
                        var1.getUnits());
                weather2.setWeatherId(weatherId);
                weather2.setLocationName(var1.getLocationName());
                weather2.setWeatherRequest(weatherRequestUrl);
                weather2.setWeatherResponse(weatherResponse);
                weather2.setForecastRequest(forecastRequestUrl);
                weather2.setForecastResponse(forecastResponse);
                weather2.setApiCalledFlag(true);
                if (createOrUpdateDbMain.equals("C") || createOrUpdateDbMain.equals("U")) {
                    if (createOrUpdateDbMain.equals("C")) {
                        logger.info("Second record is being Created. locationId: "
                                + weather2.getWeatherId().getLocationId());
                    } else if (createOrUpdateDbMain.equals("U")) {
                        logger.info("Second record is being Updated. locationId: "
                                + weather2.getWeatherId().getLocationId());
                        weather2.setCreateTime(weatherDataFromDbMain.getCreateTime());
                        weather2.setUpdateTime(LocalDateTime.now());
                    }
                    createOrUpdateWeather(weather2, createOrUpdateDbMain);
                }
            } else {
                logger.info(
                        "'call API locationId' is equal to 'response locationId'. No need to Create or Update Second record !!!");
            }

        }

        return weatherMeDtoOut;

    }

    @Override
    public WeatherMeDto findById(WeatherMeDto var1) throws Exception {

        WeatherId weatherId = serviceMapper.toWeatherId(var1);

        Optional<Weather> optionalWeather = weatherData.findById(weatherId);

        if (!optionalWeather.isPresent()) {
            throw new EntityNotFoundExceptionN10(new Exception("Error in Weather model"),
                    ErrorContants.REASON_CODE_ENTITY_NOT_FOUND);
        }

        WeatherMeDto weatherMeDto = serviceMapper.toWeatherMeDto(optionalWeather.get(),
                optionalWeather.get().getWeatherId());

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

        WeatherLocation[] locations = objectMapper.readValue(new File("city.list.json"), WeatherLocation[].class);

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

            saved++;
            savedInThisIteration++;
            if ((saved % 20000 == 0) || (saved == locations.length)) {
                logger.info("Saving - locations.length: " + locations.length + "  / saving now   : "
                        + savedInThisIteration + " records");
                locationData.iterableCreate(locationsForIterable);
                logger.info(
                        "SAVED  - locations.length: " + locations.length + "  / totally saved: " + saved + " records");
                locationsForIterable.removeAll(locationsForIterable);
                savedInThisIteration = 0;
            }

        }

        return true;

    }

    private WeatherMeDto parseCurrentWeatherJson(String currentWeatherJson) throws Exception {

        logger.info("'WeatherMeServiceImpl.parseCurrentWeatherJson' running...");

        WeatherMeDto weatherMeDto;

        ObjectMapper objectMapper = new ObjectMapper();

        CurrentWeather currentWeather = objectMapper.readValue(currentWeatherJson, CurrentWeather.class);

        weatherMeDto = new WeatherMeDto();
        weatherMeDto.setLocationId(currentWeather.getId());
        weatherMeDto.setLocationName(currentWeather.getName());
        weatherMeDto.setTimeZone(currentWeather.getTimezone());
        weatherMeDto.setDescription(currentWeather.getWeather()[0].getDescription());
        weatherMeDto.setDescriptionIcon(
                "http://openweathermap.org/img/w/" + currentWeather.getWeather()[0].getIcon() + ".png");
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

        return weatherMeDto;
    }

    private ArrayList<WeatherNearFuture> parseForecastWeatherJson(String forecastWeatherJson) throws Exception {

        logger.info("'WeatherMeServiceImpl.parseForecastWeatherJson' running...");

        ArrayList<WeatherNearFuture> weatherNearFutures = new ArrayList<>();
        WeatherNearFuture weatherNearFuture;

        ObjectMapper objectMapper = new ObjectMapper();

        ThreeHourForecastFiveDays forecastWeather = objectMapper.readValue(forecastWeatherJson,
                ThreeHourForecastFiveDays.class);

        weatherNearFuture = new WeatherNearFuture();
        weatherNearFuture.setTemp(forecastWeather.getList()[0].getMain().getTemp());
        weatherNearFuture.setDescription(forecastWeather.getList()[0].getWeather()[0].getDescription());
        weatherNearFuture.setDtTxt(forecastWeather.getList()[0].getDt_txt());
        weatherNearFutures.add(weatherNearFuture);

        weatherNearFuture = new WeatherNearFuture();
        weatherNearFuture.setTemp(forecastWeather.getList()[1].getMain().getTemp());
        weatherNearFuture.setDescription(forecastWeather.getList()[1].getWeather()[0].getDescription());
        weatherNearFuture.setDtTxt(forecastWeather.getList()[1].getDt_txt());
        weatherNearFutures.add(weatherNearFuture);

        weatherNearFuture = new WeatherNearFuture();
        weatherNearFuture.setTemp(forecastWeather.getList()[2].getMain().getTemp());
        weatherNearFuture.setDescription(forecastWeather.getList()[2].getWeather()[0].getDescription());
        weatherNearFuture.setDtTxt(forecastWeather.getList()[2].getDt_txt());
        weatherNearFutures.add(weatherNearFuture);

        weatherNearFuture = new WeatherNearFuture();
        weatherNearFuture.setTemp(forecastWeather.getList()[3].getMain().getTemp());
        weatherNearFuture.setDescription(forecastWeather.getList()[3].getWeather()[0].getDescription());
        weatherNearFuture.setDtTxt(forecastWeather.getList()[3].getDt_txt());
        weatherNearFutures.add(weatherNearFuture);

        weatherNearFuture = new WeatherNearFuture();
        weatherNearFuture.setTemp(forecastWeather.getList()[4].getMain().getTemp());
        weatherNearFuture.setDescription(forecastWeather.getList()[4].getWeather()[0].getDescription());
        weatherNearFuture.setDtTxt(forecastWeather.getList()[4].getDt_txt());
        weatherNearFutures.add(weatherNearFuture);

        weatherNearFuture = new WeatherNearFuture();
        weatherNearFuture.setTemp(forecastWeather.getList()[5].getMain().getTemp());
        weatherNearFuture.setDescription(forecastWeather.getList()[5].getWeather()[0].getDescription());
        weatherNearFuture.setDtTxt(forecastWeather.getList()[5].getDt_txt());
        weatherNearFutures.add(weatherNearFuture);

        weatherNearFuture = new WeatherNearFuture();
        weatherNearFuture.setTemp(forecastWeather.getList()[6].getMain().getTemp());
        weatherNearFuture.setDescription(forecastWeather.getList()[6].getWeather()[0].getDescription());
        weatherNearFuture.setDtTxt(forecastWeather.getList()[6].getDt_txt());
        weatherNearFutures.add(weatherNearFuture);

        weatherNearFuture = new WeatherNearFuture();
        weatherNearFuture.setTemp(forecastWeather.getList()[7].getMain().getTemp());
        weatherNearFuture.setDescription(forecastWeather.getList()[7].getWeather()[0].getDescription());
        weatherNearFuture.setDtTxt(forecastWeather.getList()[7].getDt_txt());
        weatherNearFutures.add(weatherNearFuture);

        return weatherNearFutures;
    }

    private ArrayList<WeatherNextDay> parseForecastWeatherJsonForNextDays(String forecastWeatherJson) throws Exception {

        logger.info("'WeatherMeServiceImpl.parseForecastWeatherJsonForNextDays' running...");

        ArrayList<WeatherNextDay> weatherNextDays = new ArrayList<>();
        WeatherNextDay weatherNextDay;

        ObjectMapper objectMapper = new ObjectMapper();

        ThreeHourForecastFiveDays forecastWeather = objectMapper.readValue(forecastWeatherJson,
                ThreeHourForecastFiveDays.class);

        BigDecimal tempMin = BigDecimal.valueOf(1000);
        BigDecimal tempMax = BigDecimal.valueOf(-1000);
        BigDecimal temp = BigDecimal.valueOf(0);
        BigDecimal tempTotal = BigDecimal.valueOf(0);
        long tempCount = 0;
        String prevDate = "";
        String prevDtTxt = "";
        String prevDescription = "";
        String currentDate = forecastWeather.getList()[0].getDt_txt().substring(0, 10);
        logger.info("currentDate: " + currentDate);

        int addCount = 0;
        Boolean nextDays = false;
        ForecastInfo[] forecastInfos = forecastWeather.getList();
        for (ForecastInfo forecastInfo : forecastInfos) {
            if (!nextDays) {
                if (!forecastInfo.getDt_txt().substring(0, 10).equals(currentDate)) {
                    logger.info("currentDate : " + currentDate);
                    logger.info("tempCount : " + tempCount);

                    nextDays = true;
                    prevDate = forecastInfo.getDt_txt().substring(0, 10);
                    prevDtTxt = forecastInfo.getDt_txt();
                    prevDescription = forecastInfo.getWeather()[0].getDescription();
                    tempCount = 0;
                } else
                    tempCount++;
            }

            if (nextDays) {

                if (forecastInfo.getDt_txt().substring(0, 10).equals(prevDate)) {

                    if (BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp_min()))
                            .compareTo(tempMin) == -1) {
                        tempMin = BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp_min()));
                    }

                    if (BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp_max()))
                            .compareTo(tempMax) == 1) {
                        tempMax = BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp_max()));
                    }

                    temp = BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp()));
                    tempTotal = tempTotal.add(temp);

                    tempCount++;

                } else {
                    logger.info("Date : " + prevDtTxt);
                    logger.info("tempCount : " + tempCount);

                    weatherNextDay = new WeatherNextDay();
                    weatherNextDay.setTemp(
                            tempTotal.divide(BigDecimal.valueOf(tempCount), 2, RoundingMode.HALF_DOWN).toString());
                    weatherNextDay.setTempMin(tempMin.toString());
                    weatherNextDay.setTempMax(tempMax.toString());
                    weatherNextDay.setDescription(prevDescription);
                    weatherNextDay.setDtTxt(prevDtTxt);
                    if (addCount < 4) {
                        weatherNextDays.add(weatherNextDay);
                        addCount++;
                    }

                    tempMin = BigDecimal.valueOf(1000);
                    tempMax = BigDecimal.valueOf(-1000);
                    prevDate = forecastInfo.getDt_txt().substring(0, 10);
                    prevDtTxt = forecastInfo.getDt_txt();
                    prevDescription = forecastInfo.getWeather()[0].getDescription();
                    tempTotal = BigDecimal.valueOf(0);
                    tempCount = 0;

                    if (BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp_min()))
                            .compareTo(tempMin) == -1) {
                        tempMin = BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp_min()));
                    }

                    if (BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp_max()))
                            .compareTo(tempMax) == 1) {
                        tempMax = BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp_max()));
                    }

                    temp = BigDecimal.valueOf(Double.parseDouble(forecastInfo.getMain().getTemp()));
                    tempTotal = tempTotal.add(temp);

                    tempCount++;

                }

            }
        }

        logger.info("Date : " + prevDtTxt);
        logger.info("tempCount : " + tempCount);

        weatherNextDay = new WeatherNextDay();
        weatherNextDay.setTemp(tempTotal.divide(BigDecimal.valueOf(tempCount), 2, RoundingMode.HALF_DOWN).toString());
        weatherNextDay.setTempMin(tempMin.toString());
        weatherNextDay.setTempMax(tempMax.toString());
        weatherNextDay.setDescription(prevDescription);
        weatherNextDay.setDtTxt(prevDtTxt);
        if (addCount < 4) {
            weatherNextDays.add(weatherNextDay);
            addCount++;
        }

        return weatherNextDays;
    }

    private String callCurrentWeatherApi(WeatherMeDto var1) {

        logger.info("'WeatherMeServiceImpl.callCurrentWeatherApi' running...");

        String apiUrl = weatherMeConfigurationProperties.getApiUrl();
        String appId = weatherMeConfigurationProperties.getApiAppid();
        String currentWeatherSuffix = weatherMeConfigurationProperties.getApiSuffixForCurrentWeather();

        weatherRequestUrl = apiUrl + currentWeatherSuffix + "q=" + var1.getLocationName() + "&lang="
                + var1.getLanguage() + "&units=" + var1.getUnits() + "&appid=" + appId;

        String response = "";
        try {
            logger.info("calling CurrentWeather API !!");
            response = restTemplate.getForObject(weatherRequestUrl, String.class);
            logger.info("returned response from CurrentWeather API !!");
        } catch (HttpClientErrorException e) {
            new HttpExceptionDispatcher().dispatchToException(e);
        }

        return response;

    }

    private String callForecastWeatherApi(WeatherMeDto var1) {

        logger.info("'WeatherMeServiceImpl.callForecastWeatherApi' running...");

        String apiUrl = weatherMeConfigurationProperties.getApiUrl();
        String appId = weatherMeConfigurationProperties.getApiAppid();
        String forecastWeatherSuffix = weatherMeConfigurationProperties.getApiSuffixForForecastWeather();

        forecastRequestUrl = apiUrl + forecastWeatherSuffix + "q=" + var1.getLocationName() + "&lang="
                + var1.getLanguage() + "&units=" + var1.getUnits() + "&cnt=" + "40" + "&appid=" + appId;

        String response = "";
        try {
            logger.info("calling ForecastWeather API !!");
            response = restTemplate.getForObject(forecastRequestUrl, String.class);
            logger.info("returned response from ForecastWeather API !!");
        } catch (HttpClientErrorException e) {
            new HttpExceptionDispatcher().dispatchToException(e);
        }

        return response;

    }

    private void createOrUpdateWeather(Weather weather, String createOrUpdate) throws Exception {
        Weather savedWeather = null;
        if (createOrUpdate.equals("C")) {
            savedWeather = weatherData.create(weather);
            logger.info("Weather Record has been Created. locationId: " + weather.getWeatherId().getLocationId());
        } else if (createOrUpdate.equals("U")) {
            savedWeather = weatherData.update(weather);
            logger.info("Weather Record has been Updated. locationId: " + weather.getWeatherId().getLocationId());
        } else {
            logger.info("'createOrUpdate' not valid. So, Weather Record has not been Created or Updated. locationId: "
                    + weather.getWeatherId().getLocationId());
        }

        if (savedWeather != null) {
            LocalDateTime localDateTime = LocalDateTime.now();

            logger.info("WeatherHistory Record is being Created. locationId: "
                    + savedWeather.getWeatherId().getLocationId() + " historyCreateTime : " + localDateTime);
            WeatherHistoryId weatherHistoryId = new WeatherHistoryId(savedWeather.getWeatherId().getLocationId(),
                    savedWeather.getWeatherId().getLanguage(), savedWeather.getWeatherId().getUnits(), localDateTime,
                    0);
            WeatherHistory weatherHistory = new WeatherHistory();
            weatherHistory.setWeatherHistoryId(weatherHistoryId);
            weatherHistory.setCreateTime(savedWeather.getCreateTime());
            weatherHistory.setLocationName(savedWeather.getLocationName());
            weatherHistory.setWeatherRequest(savedWeather.getWeatherRequest());
            weatherHistory.setWeatherResponse(savedWeather.getWeatherResponse());
            weatherHistory.setForecastRequest(savedWeather.getForecastRequest());
            weatherHistory.setForecastResponse(savedWeather.getForecastResponse());
            weatherHistory.setUpdateTime(savedWeather.getUpdateTime());
            weatherHistory.setApiCalledFlag(savedWeather.getApiCalledFlag());

            weatherHistoryData.create(weatherHistory);

            logger.info("WeatherHistory Record has been Created. !!!");
        }
    }

}
