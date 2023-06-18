package ru.smartup.copycat.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "crawler")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CrawlerConfig {
    private long pollDelay;
}
