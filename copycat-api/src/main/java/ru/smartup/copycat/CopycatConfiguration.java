package ru.smartup.copycat;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.smartup.utils.config.CloudConfig;

@Configuration
@ConfigurationPropertiesScan({"ru.smartup.copycat", "ru.smartup.utils"})
@AllArgsConstructor
public class CopycatConfiguration {
    private CloudConfig cloudConfig;
    private Info getInfo() {
        return new Info().title("Copycatcher REST API").
                description("APIs for Copycatcher");
    }

    @Bean
    public OpenAPI openAPIBean() {
        return new OpenAPI().info(getInfo());
    }
}
