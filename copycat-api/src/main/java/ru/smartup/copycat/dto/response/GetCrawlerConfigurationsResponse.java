package ru.smartup.copycat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetCrawlerConfigurationsResponse {
    private List<GetCrawlerConfigurationResponse> configurations = new ArrayList<>();
}
