package com.alperkurtul.weatherme.contract.impl;

import com.alperkurtul.weatherme.bean.TemplateResponse;
import com.alperkurtul.weatherme.bean.TemplateRequest;
import com.alperkurtul.weatherme.contract.TemplateService;
import com.alperkurtul.weatherme.contract.TemplateRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateRestControllerImpl implements TemplateRestController {

    @Autowired
    private TemplateService templateService;

    @Override
    public TemplateResponse getTemplate(String key) throws Exception{

        TemplateResponse templateResponse = (TemplateResponse) templateService.getCurrentWeather(new TemplateRequest("Istanbul", "tr", "metric"));

        return templateResponse;
    }

    @Override
    public TemplateResponse setTemplate(TemplateRequest request) throws Exception {

        TemplateResponse templateResponse = (TemplateResponse) templateService.getCurrentWeather(new TemplateRequest("Istanbul", "tr", "metric"));

        return templateResponse;
    }

}
