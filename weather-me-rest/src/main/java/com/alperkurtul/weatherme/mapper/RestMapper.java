package com.alperkurtul.weatherme.mapper;

import com.alperkurtul.weatherme.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RestMapper {

    RestMapper INSTANCE = Mappers.getMapper(RestMapper.class);

    WeatherMeDto toWeatherMeDto(WeatherMeRequest output);

    CurrentWeatherResponse toCurrentWeatherResponse(WeatherMeDto output);

    LocationResponse toLocationResponse(LocationDto output);

}
