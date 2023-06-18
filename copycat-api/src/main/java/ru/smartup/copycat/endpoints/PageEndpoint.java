package ru.smartup.copycat.endpoints;

import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.smartup.copycat.dto.response.GetIndexingStatusResponse;
import ru.smartup.copycat.dto.response.GetSimilarPagesResponse;
import ru.smartup.copycat.services.PageService;

/**
 * <p>
 *     Endpoint which handles user requests with pages
 * </p>*/
@RestController
@RequestMapping("/api/pages")
@Validated
public class PageEndpoint {
    private final PageService pageService;

    public PageEndpoint(PageService pageService) { this.pageService = pageService; }

    @PutMapping(value="/start")
    public void startIndexingPages() {
        pageService.startIndexingPages();
    }

    @GetMapping(value="/status")
    public GetIndexingStatusResponse getIndexingStatus() {
        return pageService.getIndexingStatus();
    }

    @GetMapping
    public GetSimilarPagesResponse getSimilarPages(@URL String url) {
        return pageService.getSimilarPages(url);
    }
}
