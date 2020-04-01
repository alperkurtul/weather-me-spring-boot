package com.alperkurtul.weatherme.data.error.handling;

import com.alperkurtul.weatherme.data.constant.ApplicationConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageAccessor {

    @Value("${spring.profiles.active}")
    private String environment;

    private static Logger logger = LogManager.getLogger(MessageAccessor.class);

    private MessageSource messageSource;

    @Autowired
    public MessageAccessor(MessageSource source) {
        this.messageSource = source;
    }

    public String getMessage(int returnCode, int reasonCode) {
        String key = getMessageKey(returnCode, reasonCode);
        return getMessage(key, null);
    }

    public String getMessage(String key, String... args) {
        Locale locale = Locale.forLanguageTag(ApplicationConstants.DEFAULT_DIALECT);

        try {
            locale = LocaleContextHolder.getLocale();
        } catch (Exception e) {
            logger.error("LocaleContextHolder.getLocale() error. Default locale is setted. Key: " + key + " Locale: " + locale.getLanguage());
        }

        String messageText = null;
        try {
            messageText = messageSource.getMessage(key, args, locale);
            if (messageText == null || messageText.isEmpty()) {
                messageText = messageSource.getMessage("DEFAULT_ERROR_MESSAGE", args, locale);
                logger.error("The message isnot in the message file. Key: " + key + " Locale: " + locale.getLanguage());
            }
        } catch (NoSuchMessageException e) {
            logger.error("The message isnot in the message file. Key: " + key + " Locale: " + locale.getLanguage());
            messageText = "Sorry, we cannot perform your request at the moment. Please try again later.";
        }

        return messageText;
    }

    private static String getMessageKey(int returnCode, int reasonCode) {
        return new StringBuilder("N")
                .append(String.format("%05d", Math.abs(returnCode)))
                .append("-")
                .append(String.format("%05d", reasonCode)).toString();
    }

}
