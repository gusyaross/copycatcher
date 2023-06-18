package ru.smartup.copycat.dto.response;

import lombok.Data;
import ru.smartup.models.HistoryStatus;

@Data
public class CrawlerHistoryResponse {
    private String startTime;
    private String endTime;

    private int totalPages;
    private int pagesSkipped;
    private int pagesFailed;
    private int pagesFetched;

    private HistoryStatus runStatus;
}
