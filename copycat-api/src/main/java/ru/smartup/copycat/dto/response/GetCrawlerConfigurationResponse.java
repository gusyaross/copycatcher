package ru.smartup.copycat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.smartup.models.StateType;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetCrawlerConfigurationResponse {
    private String name;
    private List<String> startingPoints = new ArrayList<>();
    private StateType status;
    private CrawlerHistoryResponse lastRun;
}
