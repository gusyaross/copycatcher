package ru.smartup.copycat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"ru.smartup.copycat", "ru.smartup.utils", "ru.smartup.models"})
@EntityScan({"ru.smartup.copycat", "ru.smartup.utils", "ru.smartup.models"})
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
