package ru.smartup.copycat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.smartup.copycat.validations.URLs;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCrawlerConfigurationRequest {
    @URLs
    private List<String> startingPoints = new ArrayList<>();
}
