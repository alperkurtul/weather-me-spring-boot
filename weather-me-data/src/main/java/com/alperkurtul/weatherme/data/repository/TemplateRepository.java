package com.alperkurtul.weatherme.data.repository;

import com.alperkurtul.weatherme.data.model.Template;
import com.alperkurtul.weatherme.data.model.TemplateId;
import org.springframework.data.repository.CrudRepository;

public interface TemplateRepository extends CrudRepository<Template, TemplateId> {
}
