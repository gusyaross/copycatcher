package ru.smartup.copycat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import ru.smartup.copycat.endpoints.PageEndpoint;


import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(PageEndpoint.class)
public class TestPageController extends BaseTest {

    @Test
    public void testStartIndexing() throws Exception {
        MvcResult result = httpPut("/api/pages/start");

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void testIndexingStatus() throws Exception {
        MvcResult result = httpGet("/api/pages/status");

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }
}
