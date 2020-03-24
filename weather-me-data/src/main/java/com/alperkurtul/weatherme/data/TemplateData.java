package com.alperkurtul.weatherme.data;

import com.alperkurtul.weatherme.data.model.Template;
import com.alperkurtul.weatherme.data.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateData {

    @Autowired
    private TemplateRepository templateRepository;

    public void saveTemplate(Template template) {

        templateRepository.save(template);

    }

}
