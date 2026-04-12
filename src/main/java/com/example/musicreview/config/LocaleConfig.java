package com.example.musicreview.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver clr = new CookieLocaleResolver("musicreview-locale");
        clr.setDefaultLocale(Locale.ENGLISH);
        clr.setCookieMaxAge(java.time.Duration.ofDays(365));
        return clr;
    }
}
