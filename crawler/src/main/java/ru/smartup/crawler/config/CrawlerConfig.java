package ru.smartup.crawler.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "crawler")
@ConfigurationPropertiesScan({"ru.smartup.crawler"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CrawlerConfig {
    private long tasksCheckPeriod;
}