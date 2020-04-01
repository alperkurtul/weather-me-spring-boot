package com.alperkurtul.weatherme.constant;

import org.springframework.beans.factory.annotation.Value;

public final class ApplicationConstants {

    @Value("${spring.profiles.active}")
    private String environment;

    public static final String DEFAULT_DIALECT = "tr-TR";

}
