package ru.smartup.copycat;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.smartup.copycat.services.CrawlerService;
import ru.smartup.copycat.services.PageService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public abstract class BaseTest {
    protected final Gson gson = new Gson();
    @Autowired
    private MockMvc mvc;

    @MockBean
    private PageService pageService;

    @MockBean
    private CrawlerService crawlerService;

    private final String host = "http://localhost:8080";

    protected MvcResult httpPost(String path) throws Exception {
        return mvc.perform(post(host + path)).andReturn();
    }

    protected MvcResult httpPost(String path, String jsonBody) throws Exception {
        return mvc.perform(post(host + path).contentType(MediaType.APPLICATION_JSON).content(jsonBody)).andReturn();
    }

    protected MvcResult httpPut(String path, String jsonBody) throws Exception {
        return mvc.perform(put(host + path).contentType(MediaType.APPLICATION_JSON).content(jsonBody)).andReturn();
    }

    protected MvcResult httpPut(String path) throws Exception {
        return mvc.perform(put(host + path)).andReturn();
    }

    protected MvcResult httpGet(String path) throws Exception {
        return mvc.perform(get(host + path)).andReturn();
    }
}
