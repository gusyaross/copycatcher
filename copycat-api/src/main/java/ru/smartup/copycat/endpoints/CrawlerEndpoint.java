package ru.smartup.copycat.endpoints;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.smartup.copycat.dto.request.CreateCrawlerConfigurationRequest;
import ru.smartup.copycat.dto.request.UpdateCrawlerConfigurationRequest;
import ru.smartup.copycat.dto.response.CreateCrawlerConfigurationResponse;
import ru.smartup.copycat.dto.response.GetCrawlerConfigurationResponse;
import ru.smartup.copycat.dto.response.GetCrawlerConfigurationsResponse;
import ru.smartup.copycat.dto.response.UpdateCrawlerConfigurationResponse;
import ru.smartup.copycat.exceptions.ClientException;
import ru.smartup.copycat.exceptions.EntityNotFoundException;
import ru.smartup.copycat.exceptions.UnableToStartCrawlerException;
import ru.smartup.models.StateType;
import ru.smartup.copycat.services.CrawlerService;

import javax.validation.Valid;
import java.io.IOException;

/**
 * <p>
 *     Endpoint which handles user requests to crawler app
 * </p>*/
@RestController
@RequestMapping("/api/crawlers")
@Validated
public class CrawlerEndpoint {
    private final CrawlerService crawlerService;

    public CrawlerEndpoint(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @PostMapping
    public CreateCrawlerConfigurationResponse createCrawlerConfiguration(@RequestBody @Valid CreateCrawlerConfigurationRequest request) throws ClientException {
        return crawlerService.createCrawlerConfiguration(request);
    }

    @PutMapping(value = "/start/{crawlerName}")
    public void startCrawler(@PathVariable String crawlerName) throws UnableToStartCrawlerException {
        crawlerService.startCrawler(crawlerName);
    }

    @PutMapping(value = "/{crawlerName}")
    public UpdateCrawlerConfigurationResponse updateCrawlerConfiguration(@PathVariable String crawlerName,
                                                                         @RequestBody @Valid UpdateCrawlerConfigurationRequest request) throws ClientException {
        return crawlerService.updateCrawlerConfiguration(request, crawlerName);
    }

    @GetMapping(value = "/{crawlerName}")
    public GetCrawlerConfigurationResponse getCrawlerConfiguration(@PathVariable String crawlerName) throws IOException, EntityNotFoundException {
        return crawlerService.getCrawlerConfiguration(crawlerName);
    }

    @GetMapping
    public GetCrawlerConfigurationsResponse getCrawlerConfigurations(@RequestParam(required = false) StateType status) throws IOException {
        return crawlerService.getCrawlerConfigurations(status);
    }
}