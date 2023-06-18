package ru.smartup.copycat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import ru.smartup.copycat.dto.request.CreateCrawlerConfigurationRequest;
import ru.smartup.copycat.dto.request.UpdateCrawlerConfigurationRequest;
import ru.smartup.copycat.dto.response.CreateCrawlerConfigurationResponse;
import ru.smartup.copycat.dto.response.ErrorDtoResponse;
import ru.smartup.copycat.endpoints.CrawlerEndpoint;
import ru.smartup.copycat.exceptions.GlobalErrorHandler;
import ru.smartup.copycat.services.CrawlerService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(CrawlerEndpoint.class)
class TestCrawlerController extends BaseTest {

    @Test
    public void testCreateCrawler() throws Exception {
        List<String> startingPoints = new ArrayList<>();
        startingPoints.add("https://smartup.ru");

        CreateCrawlerConfigurationRequest request = new CreateCrawlerConfigurationRequest("crawler", startingPoints);
        MvcResult result = httpPost("/api/crawlers", gson.toJson(request));

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        CreateCrawlerConfigurationResponse response = gson.fromJson(
                result.getResponse().getContentAsString(),
                CreateCrawlerConfigurationResponse.class
        );
    }

    @Test
    public void testCreateCrawlerBadRequestsEmptyList() throws Exception {
        List<String> startingPoints = new ArrayList<>();
        CreateCrawlerConfigurationRequest request = new CreateCrawlerConfigurationRequest("crawler", startingPoints);
        MvcResult result = httpPost("/api/crawlers", gson.toJson(request));
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        ErrorDtoResponse errorDtoResponse = gson.fromJson(result.getResponse().getContentAsString(), ErrorDtoResponse.class);
        assertEquals(1, errorDtoResponse.getErrors().size());
        assertTrue(errorDtoResponse.getErrors().get(0).getMessage().contains("empty"));
    }

    @Test
    public void testUpdateCrawlerBadRequestsEmptyList() throws Exception {
        List<String> startingPoints = new ArrayList<>();
        UpdateCrawlerConfigurationRequest request = new UpdateCrawlerConfigurationRequest(startingPoints);
        MvcResult result = httpPut("/api/crawlers/crawler", gson.toJson(request));
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        ErrorDtoResponse errorDtoResponse = gson.fromJson(result.getResponse().getContentAsString(), ErrorDtoResponse.class);
        assertEquals(1, errorDtoResponse.getErrors().size());
        assertTrue(errorDtoResponse.getErrors().get(0).getMessage().contains("empty"));
    }

    @Test
    public void testCreateCrawlerBadRequestIllegalStartingPoints() throws Exception {
        String expectedErrorMessage = "starting point does not match url pattern";
        List<String> illegalStartingPoints = new ArrayList<>();
        illegalStartingPoints.add("");
        illegalStartingPoints.add("smartup");
        illegalStartingPoints.add("a.b");
        illegalStartingPoints.add(".");

        for (String illegalStartingPoint : illegalStartingPoints) {
            List<String> startingPoints = new ArrayList<>();
            startingPoints.add(illegalStartingPoint);
            CreateCrawlerConfigurationRequest request = new CreateCrawlerConfigurationRequest(
                    "crawler", startingPoints);
            MvcResult result = httpPost("/api/crawlers", gson.toJson(request));
            assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

            ErrorDtoResponse errorDtoResponse = gson.fromJson(result.getResponse().getContentAsString(), ErrorDtoResponse.class);
            assertEquals(1, errorDtoResponse.getErrors().size());

            GlobalErrorHandler.Error error = errorDtoResponse.getErrors().get(0);
            assertEquals(expectedErrorMessage, error.getMessage());
        }
    }

    @Test
    public void testUpdateCrawlerBadRequestIllegalStartingPoints() throws Exception {
        String expectedErrorMessage = "starting point does not match url pattern";
        List<String> illegalStartingPoints = new ArrayList<>();
        illegalStartingPoints.add("");
        illegalStartingPoints.add("smartup");
        illegalStartingPoints.add("a.b");
        illegalStartingPoints.add(".");

        for (String illegalStartingPoint : illegalStartingPoints) {
            List<String> startingPoints = new ArrayList<>();
            startingPoints.add(illegalStartingPoint);
            UpdateCrawlerConfigurationRequest request = new UpdateCrawlerConfigurationRequest(startingPoints);
            MvcResult result = httpPut("/api/crawlers/crawler", gson.toJson(request));
            assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

            ErrorDtoResponse errorDtoResponse = gson.fromJson(result.getResponse().getContentAsString(), ErrorDtoResponse.class);
            assertEquals(1, errorDtoResponse.getErrors().size());

            GlobalErrorHandler.Error error = errorDtoResponse.getErrors().get(0);
            assertEquals(expectedErrorMessage, error.getMessage());
        }
    }

    @Test
    public void testCreateCrawlerBadRequestIllegalCrawlerName() throws Exception {
        List<String> startingPoints = new ArrayList<>();
        startingPoints.add("https://smartup.ru");

        CreateCrawlerConfigurationRequest request = new CreateCrawlerConfigurationRequest("", startingPoints);
        MvcResult result = httpPost("/api/crawlers", gson.toJson(request));
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        ErrorDtoResponse errorDtoResponse = gson.fromJson(result.getResponse().getContentAsString(), ErrorDtoResponse.class);
        assertEquals(1, errorDtoResponse.getErrors().size());

        assertTrue(errorDtoResponse.getErrors().get(0).getMessage().contains("must not be blank"));
    }
}