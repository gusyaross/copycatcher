package ru.smartup.copycat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.smartup.copycat.validations.URLs;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCrawlerConfigurationRequest {
    @NotBlank
    private String name;

    @URLs
    private List<String> startingPoints = new ArrayList<>();
}