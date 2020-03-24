package com.alperkurtul.weatherme.contract;

public interface TemplateService<REQ, RES> {

    //@Bean
    public RES getCurrentWeather(REQ var1);

}
