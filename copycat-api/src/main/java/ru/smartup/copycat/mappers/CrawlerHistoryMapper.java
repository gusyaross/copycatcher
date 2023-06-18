package ru.smartup.copycat.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.smartup.copycat.dto.response.CrawlerHistoryResponse;
import ru.smartup.models.CrawlerHistory;

@Mapper(componentModel = "spring")
public interface CrawlerHistoryMapper {
    @Mapping(source = "status", target = "runStatus")
    CrawlerHistoryResponse modelToResponse(CrawlerHistory crawlerHistory);
}
