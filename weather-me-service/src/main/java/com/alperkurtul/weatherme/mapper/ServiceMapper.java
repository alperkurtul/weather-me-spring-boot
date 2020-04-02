package com.alperkurtul.weatherme.mapper;

import com.alperkurtul.weatherme.model.Weather;
import com.alperkurtul.weatherme.model.WeatherId;
import com.alperkurtul.weatherme.model.WeatherMeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceMapper {

    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    WeatherId toWeatherId(WeatherMeDto output);

    Weather toWeather(WeatherMeDto output);

    WeatherMeDto toTemplateDto(Weather output1, WeatherId output2);

}
