package ru.smartup.copycat.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.smartup.copycat.dto.request.CreateCrawlerConfigurationRequest;
import ru.smartup.copycat.dto.response.CreateCrawlerConfigurationResponse;
import ru.smartup.models.CrawlerConfiguration;

@Mapper(componentModel = "spring")
public interface CrawlerConfigurationMapper {

    CrawlerConfiguration requestToModel(CreateCrawlerConfigurationRequest request);

    @Mapping(target = "id", ignore = true)
    CreateCrawlerConfigurationResponse modelToResponse(CrawlerConfiguration crawlerConfiguration);
}
