package ru.smartup.copycat.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCrawlerConfigurationResponse {
    private List<String> startingPoints = new ArrayList<>();
}
