package ru.smartup.utils.config;

import lombok.*;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MessageQueueConfig {
    private int messageSizeLimit;
    private String queueName;
    private int pollDelay;
}