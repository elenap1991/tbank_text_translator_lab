package com.pirogova.translator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "translator")
public class AppConfig {
    /**
     * API-ключ для подключения к YandexCloud
     */
    private String apiKey;

    /**
     * Максимальное количество потоков
     */
    private int maxThreadCount;

    /**
     * Таймаут в миллисекундах
     */
    private int timeoutMilliseconds;
}

