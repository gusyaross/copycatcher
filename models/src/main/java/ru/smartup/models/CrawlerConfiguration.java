package ru.smartup.models;

import lombok.*;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CrawlerConfiguration {
    @NonNull
    private String name;

    @NonNull
    private List<String> startingPoints;
}
