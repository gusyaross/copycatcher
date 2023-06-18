package ru.smartup.utils.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties("yandex-cloud")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CloudConfig {
    private String accessKey;
    private String secretKey;
    private MessageQueueConfig mq;
    private S3Config s3;
}
