package ru.smartup.copycat.dto.response;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateCrawlerConfigurationResponse {
    private long id;
    private String name;
    private List<String> startingPoints = new ArrayList<>();
}
