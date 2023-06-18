package ru.smartup.utils.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class S3Config {
    private String bucket;
}
