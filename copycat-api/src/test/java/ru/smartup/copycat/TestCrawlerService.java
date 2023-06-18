package ru.smartup.copycat;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.smartup.copycat.dao.CrawlerHistoryRepository;
import ru.smartup.copycat.dao.CrawlerStateRepository;
import ru.smartup.copycat.dto.request.CreateCrawlerConfigurationRequest;
import ru.smartup.copycat.dto.request.UpdateCrawlerConfigurationRequest;
import ru.smartup.copycat.exceptions.EntityNotFoundException;
import ru.smartup.copycat.mappers.CrawlerConfigurationMapperImpl;
import ru.smartup.copycat.mappers.CrawlerHistoryMapperImpl;
import ru.smartup.models.*;
import ru.smartup.copycat.services.CrawlerService;
import ru.smartup.utils.MessageQueue;
import ru.smartup.utils.S3Storage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestCrawlerService {
    private Gson gson = new Gson();
    private CrawlerStateRepository crawlerStateRepository = Mockito.mock(CrawlerStateRepository.class);
    private CrawlerHistoryRepository crawlerHistoryRepository = Mockito.mock(CrawlerHistoryRepository.class);
    private S3Storage s3Storage = Mockito.mock(S3Storage.class);
    private MessageQueue messageQueue = Mockito.mock(MessageQueue.class);
    private CrawlerService crawlerService;

    @BeforeEach
    public void setUp() {
        crawlerService = new CrawlerService(crawlerStateRepository, crawlerHistoryRepository, new CrawlerConfigurationMapperImpl(), new CrawlerHistoryMapperImpl(), s3Storage, messageQueue);
    }

    @Test
    public void testCreateCrawlerConfiguration() {
        List<String> startingPoints = new ArrayList<>();
        startingPoints.add("https://smartup.ru");

        CreateCrawlerConfigurationRequest request = new CreateCrawlerConfigurationRequest("crawler", startingPoints);

        assertDoesNotThrow(() -> crawlerService.createCrawlerConfiguration(request));
        verify(crawlerStateRepository).save(any());
        verify(s3Storage).putObject(any(), any());
    }

    @Test
    public void testStartCrawler() throws IOException {
        List<String> startingPoints = new ArrayList<>();
        startingPoints.add("https://smartup.ru");
        CrawlerState crawlerState = new CrawlerState("crawler", StateType.INACTIVE, new ArrayList<>());
        CrawlerConfiguration crawlerConfiguration = new CrawlerConfiguration("crawler", startingPoints);

        when(crawlerStateRepository.findByCrawlerNameEquals("crawler")).thenReturn(Optional.of(crawlerState));
        when(s3Storage.getObject("crawler")).thenReturn(gson.toJson(crawlerConfiguration));
        assertDoesNotThrow(() -> crawlerService.startCrawler("crawler"));
        verify(crawlerStateRepository).findByCrawlerNameEquals("crawler");
    }

    @Test
    public void testCorrectUpdateCrawlerConfiguration() {
        List<String> startingPoints = new ArrayList<>();
        startingPoints.add("https://smartup.ru");

        UpdateCrawlerConfigurationRequest request = new UpdateCrawlerConfigurationRequest(startingPoints);
        CrawlerState crawlerState = new CrawlerState();
        when(crawlerStateRepository.findByCrawlerNameEquals("crawler")).thenReturn(Optional.of(crawlerState));
        assertDoesNotThrow(() -> crawlerService.updateCrawlerConfiguration(request,"crawler"));
        verify(s3Storage).putObject(any(), any());
    }

    @Test
    public void testUpdateCrawlerConfigurationNotFound() {
        List<String> startingPoints = new ArrayList<>();
        startingPoints.add("https://smartup.ru");

        UpdateCrawlerConfigurationRequest request = new UpdateCrawlerConfigurationRequest(startingPoints);
        when(crawlerStateRepository.findByCrawlerNameEquals("crawler")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> crawlerService.updateCrawlerConfiguration(request, "crawler"));
    }

    @Test
    public void testGetCrawlerConfigurationNotFound() {
        when(crawlerStateRepository.findByCrawlerNameEquals("crawler")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> crawlerService.getCrawlerConfiguration("crawler"));
    }

    @Test
    public void testGetCrawlerHistoryNotFound() {
        CrawlerState state = new CrawlerState();
        when(crawlerStateRepository.findByCrawlerNameEquals("crawler")).thenReturn(Optional.of(state));
        when(crawlerHistoryRepository.findTopByCrawlerStateOrderByStartTimeDesc(state)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> crawlerService.getCrawlerConfiguration("crawler"));
    }

    @Test
    public void testCorrectGetCrawlerConfiguration() throws IOException {
        List<String> startingPoints = new ArrayList<>();
        startingPoints.add("https://smartup.ru");
        CrawlerState crawlerState = new CrawlerState("crawler", StateType.ACTIVE, new ArrayList<>());
        crawlerState.getHistory().add(new CrawlerHistory(1, crawlerState, LocalDateTime.now(), LocalDateTime.now(), 1, 0, 0, 0, HistoryStatus.IN_PROGRESS));
        CrawlerConfiguration crawlerConfiguration = new CrawlerConfiguration("crawler", startingPoints);

        when(crawlerStateRepository.findByCrawlerNameEquals("crawler")).thenReturn(Optional.of(crawlerState));
        when(s3Storage.getObject("crawler")).thenReturn(gson.toJson(crawlerConfiguration));
        when(crawlerHistoryRepository.findTopByCrawlerStateOrderByStartTimeDesc(crawlerState)).thenReturn(Optional.of(crawlerState.getHistory().get(0)));
        assertDoesNotThrow(() -> crawlerService.getCrawlerConfiguration("crawler"));

        verify(crawlerStateRepository).findByCrawlerNameEquals("crawler");
        verify(s3Storage).getObject("crawler");
    }

    @Test
    public void testGetCrawlerConfigurations() throws IOException {
        List<String> startingPoints = new ArrayList<>();
        startingPoints.add("https://smartup.ru");
        CrawlerState crawlerState = new CrawlerState("crawler", StateType.ACTIVE, new ArrayList<>());
        crawlerState.getHistory().add(new CrawlerHistory(1, crawlerState, LocalDateTime.now(), LocalDateTime.now(), 1, 0, 0, 0, HistoryStatus.IN_PROGRESS));
        List<CrawlerState> states = new ArrayList<>();
        states.add(crawlerState);
        CrawlerConfiguration crawlerConfiguration = new CrawlerConfiguration("crawler", startingPoints);

        when(crawlerStateRepository.findAllByStatus(StateType.ACTIVE)).thenReturn(states);
        when(s3Storage.getObject("crawler")).thenReturn(gson.toJson(crawlerConfiguration));
        when(crawlerHistoryRepository.findTopByCrawlerStateOrderByStartTimeDesc(crawlerState)).thenReturn(Optional.of(crawlerState.getHistory().get(0)));

        assertDoesNotThrow(() -> crawlerService.getCrawlerConfigurations(StateType.ACTIVE));

        verify(crawlerStateRepository).findAllByStatus(StateType.ACTIVE);
        verify(s3Storage).getObject("crawler");
    }

}
