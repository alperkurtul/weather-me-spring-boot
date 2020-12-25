package com.alperkurtul.weatherme.mapper;

import com.alperkurtul.weatherme.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceMapper {

    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    WeatherId toWeatherId(WeatherMeDto output);

    /*Weather toWeather(WeatherMeDto output);*/

    WeatherMeDto toWeatherMeDto(Weather output1, WeatherId output2);

    LocationDto toLocationDto(Location output);

}
