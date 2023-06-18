package ru.smartup.copycat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetSimilarPagesResponse {
    private Map<String, Float> urlToScore = new LinkedHashMap<>();
}
