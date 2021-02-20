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
import com.alperkurtul.weatherme.model.Location;
import com.alperkurtul.weatherme.model.LocationDto;
import com.alperkurtul.weatherme.model.LocationModel;
import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherHistory;
import com.alperkurtul.weatherme.model.WeatherHistoryId;
import com.alperkurtul.weatherme.model.WeatherId;
import com.alperkurtul.weatherme.model.WeatherMeDto;
import com.alperkurtul.weatherme.model.WeatherNearFuture;
import com.alperkurtul.weatherme.model.WeatherNextDay;
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
            var1.setLanguage("en");
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
            // if ( true ) { // TODO : This line is for testing. Remove this line while
            if (weatherDataFromDb.getUpdateTime()
                    .plusMinutes(Integer.valueOf(weatherMeConfigurationProperties.getApiCallValidityMinuteForWeather()))
                    .isBefore(LocalDateTime.now())) {
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
    public LocationDto findAllLocationByLocationName(String locationName, String language) throws Exception {

        List<Location> locationListFromDataLayer = locationData.findAllLocationByLocationName(locationName, language);

        List<LocationModel> locationModelList = new ArrayList<>();
        for (Location locationFromDataLayer : locationListFromDataLayer) {
            LocationModel locationModel = serviceMapper.toLocationModel(locationFromDataLayer);
            locationModelList.add(locationModel);
        }

        LocationDto locationDto = new LocationDto();
        locationDto.setSearchedKeyword(locationName);
        locationDto.setLocationModelList(locationModelList);

        return locationDto;
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
        weatherMeDto.setId(currentWeather.getWeather()[0].getId());
        weatherMeDto.setMain(currentWeather.getWeather()[0].getMain());
        weatherMeDto.setDescription(currentWeather.getWeather()[0].getDescription());
        weatherMeDto
                .setIcon("http://openweathermap.org/img/wn/" + currentWeather.getWeather()[0].getIcon() + "@4x.png");
        // weatherMeDto.setIcon("http://openweathermap.org/img/w/" +
        // currentWeather.getWeather()[0].getIcon() + ".png");
        weatherMeDto.setRealTemperature(currentWeather.getMain().getTemp());
        weatherMeDto.setFeelsTemperature(currentWeather.getMain().getFeels_like());
        weatherMeDto.setMinTemperature(currentWeather.getMain().getTemp_min());
        weatherMeDto.setMaxTemperature(currentWeather.getMain().getTemp_max());
        weatherMeDto.setPressure(currentWeather.getMain().getPressure());
        weatherMeDto.setHumidity(currentWeather.getMain().getHumidity());
        weatherMeDto.setCountryCode(currentWeather.getSys().getCountry());
        weatherMeDto.setSunRise(currentWeather.getSys().getSunrise());
        weatherMeDto.setSunSet(currentWeather.getSys().getSunset());
        weatherMeDto.setVisibility(currentWeather.getVisibility());
        weatherMeDto.setWindSpeed(currentWeather.getWind().getSpeed());
        weatherMeDto.setWindDirectionDegree(currentWeather.getWind().getDeg());

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        long unixTime;
        String formattedDtm;

        unixTime = Long.valueOf(weatherMeDto.getSunRise()) + Long.valueOf(weatherMeDto.getTimeZone());
        formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
        weatherMeDto.setSunRise(formattedDtm);

        unixTime = Long.valueOf(weatherMeDto.getSunSet()) + Long.valueOf(weatherMeDto.getTimeZone());
        formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
        weatherMeDto.setSunSet(formattedDtm);

        unixTime = Long.valueOf(currentWeather.getDt()) + Long.valueOf(weatherMeDto.getTimeZone());
        formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
        weatherMeDto.setWeatherDataTime(formattedDtm);

        return weatherMeDto;
    }

    private ArrayList<WeatherNearFuture> parseForecastWeatherJson(String forecastWeatherJson) throws Exception {

        logger.info("'WeatherMeServiceImpl.parseForecastWeatherJson' running...");

        ArrayList<WeatherNearFuture> weatherNearFutures = new ArrayList<>();
        WeatherNearFuture weatherNearFuture;

        ObjectMapper objectMapper = new ObjectMapper();

        ThreeHourForecastFiveDays forecastWeather = objectMapper.readValue(forecastWeatherJson,
                ThreeHourForecastFiveDays.class);

        for (int i = 0; i <= 7; i++) {

            weatherNearFuture = new WeatherNearFuture();

            logger.info("Timezone: " + forecastWeather.getCity().getTimezone());

            weatherNearFuture.setId(forecastWeather.getList()[i].getWeather()[0].getId());
            weatherNearFuture.setMain(forecastWeather.getList()[i].getWeather()[0].getMain());
            weatherNearFuture.setDescription(forecastWeather.getList()[i].getWeather()[0].getDescription());
            weatherNearFuture.setIcon("http://openweathermap.org/img/wn/"
                    + forecastWeather.getList()[i].getWeather()[0].getIcon() + "@4x.png");
            weatherNearFuture.setTemp(forecastWeather.getList()[i].getMain().getTemp());

            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            long unixTime;
            String formattedDtm;

            unixTime = Long.valueOf(forecastWeather.getList()[i].getDt())
                    + Long.valueOf(forecastWeather.getCity().getTimezone());
            formattedDtm = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+0")).format(formatter);
            weatherNearFuture.setDtTxt(formattedDtm);

            weatherNearFutures.add(weatherNearFuture);

        }

        return weatherNearFutures;
    }

    private ArrayList<WeatherNextDay> parseForecastWeatherJsonForNextDays(String forecastWeatherJson) throws Exception {

        logger.info("'WeatherMeServiceImpl.parseForecastWeatherJsonForNextDays' running...");

        ArrayList<WeatherNextDay> weatherNextDays = new ArrayList<>();
        WeatherNextDay weatherNextDay;

        ObjectMapper objectMapper = new ObjectMapper();

        ThreeHourForecastFiveDays forecastWeather = objectMapper.readValue(forecastWeatherJson,
                ThreeHourForecastFiveDays.class);

        BigDecimal temperatureMin = BigDecimal.valueOf(1000);
        BigDecimal temperatureMax = BigDecimal.valueOf(-1000);
        BigDecimal temperatureTotal = BigDecimal.valueOf(0);
        long idTotal = 0;
        long temperatureCount = 0;

        ForecastInfo[] forecastInfos = forecastWeather.getList();
        int len = forecastInfos.length;
        logger.info("forecastInfos.length : " + len);
        for (int i = 0; i < len; i++) {

            // ***********************************************************************************

            // logger.info("i : " + i);
            // logger.info("Current Date: " + forecastInfos[i].getDt_txt().substring(0,
            // 10));
            // if (i < len - 1)
            // logger.info("Next Date: " + forecastInfos[i + 1].getDt_txt().substring(0,
            // 10));
            // else
            // logger.info("Next Date: " + "**BOŞ**");
            // logger.info("id : " + forecastInfos[i].getWeather()[0].getId());
            // logger.info("Temp : " + forecastInfos[i].getMain().getTemp());
            // logger.info("Temp_min : " + forecastInfos[i].getMain().getTemp_min());
            // logger.info("Temp_max : " + forecastInfos[i].getMain().getTemp_max());

            // ***********************************************************************************

            if (BigDecimal.valueOf(Double.parseDouble(forecastInfos[i].getMain().getTemp_min()))
                    .compareTo(temperatureMin) == -1) {
                temperatureMin = BigDecimal.valueOf(Double.parseDouble(forecastInfos[i].getMain().getTemp_min()));
            }

            if (BigDecimal.valueOf(Double.parseDouble(forecastInfos[i].getMain().getTemp_max()))
                    .compareTo(temperatureMax) == 1) {
                temperatureMax = BigDecimal.valueOf(Double.parseDouble(forecastInfos[i].getMain().getTemp_max()));
            }

            temperatureTotal = temperatureTotal
                    .add(BigDecimal.valueOf(Double.parseDouble(forecastInfos[i].getMain().getTemp())));

            idTotal = idTotal + Integer.parseInt(forecastInfos[i].getWeather()[0].getId());

            temperatureCount++;

            if ((i == (len - 1)) || (!forecastInfos[i].getDt_txt().substring(0, 10)
                    .equals(forecastInfos[i + 1].getDt_txt().substring(0, 10)))) {

                logger.info("Date : " + forecastInfos[i].getDt_txt());
                logger.info("temperatureCount : " + temperatureCount);
                // logger.info("idTotal : " + idTotal);
                // logger.info("temperatureTotal : " + temperatureTotal);
                // logger.info("temperatureMin : " + temperatureMin);
                // logger.info("temperatureMax : " + temperatureMax);

                weatherNextDay = new WeatherNextDay();
                weatherNextDay.setId(BigDecimal.valueOf(idTotal)
                        .divide(BigDecimal.valueOf(temperatureCount), 0, RoundingMode.HALF_DOWN).toString());
                weatherNextDay.setMain("");
                weatherNextDay.setDescription("");
                decideWeatherCondition(weatherNextDay);
                String iconName = weatherNextDay.getIcon();
                weatherNextDay.setIcon("http://openweathermap.org/img/wn/"
                        + iconName.substring(0, iconName.length() - 1) + "d@4x.png");
                weatherNextDay.setTemp(temperatureTotal
                        .divide(BigDecimal.valueOf(temperatureCount), 2, RoundingMode.HALF_DOWN).toString());
                weatherNextDay.setTempMin(temperatureMin.toString());
                weatherNextDay.setTempMax(temperatureMax.toString());
                weatherNextDay.setDtTxt(forecastInfos[i].getDt_txt());
                weatherNextDays.add(weatherNextDay);

                // logger.info("id-calc : " + weatherNextDay.getId());
                // logger.info("temp-calc : " + weatherNextDay.getTemp());

                temperatureMin = BigDecimal.valueOf(1000);
                temperatureMax = BigDecimal.valueOf(-1000);
                temperatureTotal = BigDecimal.valueOf(0);
                idTotal = 0;
                temperatureCount = 0;

            }

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

    private void decideWeatherCondition(WeatherNextDay wnd) {

        if (Integer.parseInt(wnd.getId()) >= 804) {
            wnd.setMain("Clouds");
            wnd.setDescription("overcast clouds: 85-100%");
            wnd.setIcon("04d");
        } else if (Integer.parseInt(wnd.getId()) >= 803) {
            wnd.setMain("Clouds");
            wnd.setDescription("broken clouds: 51-84%");
            wnd.setIcon("04d");
        } else if (Integer.parseInt(wnd.getId()) >= 802) {
            wnd.setMain("Clouds");
            wnd.setDescription("scattered clouds: 25-50%");
            wnd.setIcon("03d");
        } else if (Integer.parseInt(wnd.getId()) >= 801) {
            wnd.setMain("Clouds");
            wnd.setDescription("ew clouds: 11-25%");
            wnd.setIcon("02d");
        } else if (Integer.parseInt(wnd.getId()) >= 800) {
            wnd.setMain("Clear");
            wnd.setDescription("clear sky");
            wnd.setIcon("01d");
        } else if (Integer.parseInt(wnd.getId()) >= 781) {
            wnd.setMain("Tornado");
            wnd.setDescription("tornado");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 771) {
            wnd.setMain("Squall");
            wnd.setDescription("squalls");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 762) {
            wnd.setMain("Ash");
            wnd.setDescription("volcanic ash");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 761) {
            wnd.setMain("Dust");
            wnd.setDescription("dust");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 751) {
            wnd.setMain("Sand");
            wnd.setDescription("sand");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 741) {
            wnd.setMain("Fog");
            wnd.setDescription("fog");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 731) {
            wnd.setMain("Fog");
            wnd.setDescription("sand/ dust whirls");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 721) {
            wnd.setMain("Haze");
            wnd.setDescription("Haze");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 711) {
            wnd.setMain("Smoke");
            wnd.setDescription("Smoke");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 701) {
            wnd.setMain("Mist");
            wnd.setDescription("mist");
            wnd.setIcon("50d");
        } else if (Integer.parseInt(wnd.getId()) >= 622) {
            wnd.setMain("Snow");
            wnd.setDescription("Heavy shower snow");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 621) {
            wnd.setMain("Snow");
            wnd.setDescription("Shower snow");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 620) {
            wnd.setMain("Snow");
            wnd.setDescription("Light shower snow");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 616) {
            wnd.setMain("Snow");
            wnd.setDescription("Rain and snow");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 615) {
            wnd.setMain("Snow");
            wnd.setDescription("Light rain and snow");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 613) {
            wnd.setMain("Snow");
            wnd.setDescription("Shower sleet");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 612) {
            wnd.setMain("Snow");
            wnd.setDescription("Light shower sleet");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 611) {
            wnd.setMain("Snow");
            wnd.setDescription("Sleet");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 602) {
            wnd.setMain("Snow");
            wnd.setDescription("Heavy snow");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 601) {
            wnd.setMain("Snow");
            wnd.setDescription("Snow");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 600) {
            wnd.setMain("Snow");
            wnd.setDescription("light snow");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 531) {
            wnd.setMain("Rain");
            wnd.setDescription("ragged shower rain");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 522) {
            wnd.setMain("Rain");
            wnd.setDescription("heavy intensity shower rain");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 521) {
            wnd.setMain("Rain");
            wnd.setDescription("shower rain");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 520) {
            wnd.setMain("Rain");
            wnd.setDescription("light intensity shower rain");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 511) {
            wnd.setMain("Rain");
            wnd.setDescription("freezing rain");
            wnd.setIcon("13d");
        } else if (Integer.parseInt(wnd.getId()) >= 504) {
            wnd.setMain("Rain");
            wnd.setDescription("extreme rain");
            wnd.setIcon("10d");
        } else if (Integer.parseInt(wnd.getId()) >= 503) {
            wnd.setMain("Rain");
            wnd.setDescription("very heavy rain");
            wnd.setIcon("10d");
        } else if (Integer.parseInt(wnd.getId()) >= 502) {
            wnd.setMain("Rain");
            wnd.setDescription("heavy intensity rain");
            wnd.setIcon("10d");
        } else if (Integer.parseInt(wnd.getId()) >= 501) {
            wnd.setMain("Rain");
            wnd.setDescription("moderate rain");
            wnd.setIcon("10d");
        } else if (Integer.parseInt(wnd.getId()) >= 500) {
            wnd.setMain("Rain");
            wnd.setDescription("light rain");
            wnd.setIcon("10d");
        } else if (Integer.parseInt(wnd.getId()) >= 321) {
            wnd.setMain("Drizzle");
            wnd.setDescription("shower drizzle");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 314) {
            wnd.setMain("Drizzle");
            wnd.setDescription("heavy shower rain and drizzle");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 313) {
            wnd.setMain("Drizzle");
            wnd.setDescription("shower rain and drizzle");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 312) {
            wnd.setMain("Drizzle");
            wnd.setDescription("heavy intensity drizzle rain");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 311) {
            wnd.setMain("Drizzle");
            wnd.setDescription("drizzle rain");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 310) {
            wnd.setMain("Drizzle");
            wnd.setDescription("light intensity drizzle rain");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 302) {
            wnd.setMain("Drizzle");
            wnd.setDescription("heavy intensity drizzle");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 301) {
            wnd.setMain("Drizzle");
            wnd.setDescription("drizzle");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 300) {
            wnd.setMain("Drizzle");
            wnd.setDescription("light intensity drizzle");
            wnd.setIcon("09d");
        } else if (Integer.parseInt(wnd.getId()) >= 232) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("thunderstorm with heavy drizzle");
            wnd.setIcon("11d");
        } else if (Integer.parseInt(wnd.getId()) >= 231) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("thunderstorm with drizzle");
            wnd.setIcon("11d");
        } else if (Integer.parseInt(wnd.getId()) >= 230) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("thunderstorm with light drizzle");
            wnd.setIcon("11d");
        } else if (Integer.parseInt(wnd.getId()) >= 221) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("ragged thunderstorm");
            wnd.setIcon("11d");
        } else if (Integer.parseInt(wnd.getId()) >= 212) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("heavy thunderstorm");
            wnd.setIcon("11d");
        } else if (Integer.parseInt(wnd.getId()) >= 211) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("thunderstorm");
            wnd.setIcon("11d");
        } else if (Integer.parseInt(wnd.getId()) >= 210) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("light thunderstorm");
            wnd.setIcon("11d");
        } else if (Integer.parseInt(wnd.getId()) >= 202) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("thunderstorm with heavy rain");
            wnd.setIcon("11d");
        } else if (Integer.parseInt(wnd.getId()) >= 201) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("thunderstorm with rain");
            wnd.setIcon("11d");
        } else if (Integer.parseInt(wnd.getId()) >= 200) {
            wnd.setMain("Thunderstorm");
            wnd.setDescription("thunderstorm with light rain");
            wnd.setIcon("11d");
        }

    }

}
