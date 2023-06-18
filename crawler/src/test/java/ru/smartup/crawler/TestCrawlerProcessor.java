package ru.smartup.crawler;

import com.amazonaws.services.sqs.model.Message;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.smartup.crawler.crawler.CrawlerProcessor;
import ru.smartup.crawler.dao.CrawlerHistoryRepository;
import ru.smartup.crawler.dao.CrawlerStateRepository;
import ru.smartup.crawler.dao.PageRepository;
import ru.smartup.models.CrawlerHistory;
import ru.smartup.models.CrawlerState;
import ru.smartup.models.HistoryStatus;
import ru.smartup.models.StateType;
import ru.smartup.utils.MessageQueue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestCrawlerProcessor {
    private CrawlerProcessor crawlerProcessor;
    private final CrawlerStateRepository crawlerStateRepository = Mockito.mock(CrawlerStateRepository.class);
    private final CrawlerHistoryRepository crawlerHistoryRepository = Mockito.mock(CrawlerHistoryRepository.class);
    private final PageRepository pageRepository = Mockito.mock(PageRepository.class);
    private final MessageQueue messageQueue = Mockito.mock(MessageQueue.class);
    private final CloseableHttpClient httpClient = Mockito.mock(CloseableHttpClient.class);

    @BeforeEach
    public void setUp() {
        crawlerProcessor = new CrawlerProcessor(crawlerStateRepository, crawlerHistoryRepository, pageRepository, messageQueue, new ThreadPoolExecutor(2, 20, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10)), httpClient);
    }

    @Test
    public void testReadTasks() {
        CrawlerState crawlerState1 = new CrawlerState("crawler1", StateType.ACTIVE, new ArrayList<>());
        crawlerState1.setHistory(List.of(new CrawlerHistory(crawlerState1, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState crawlerState2 = new CrawlerState("crawler2", StateType.ACTIVE, new ArrayList<>());
        crawlerState2.setHistory(List.of(new CrawlerHistory(crawlerState2, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState crawlerState3 = new CrawlerState("crawler3", StateType.ACTIVE, new ArrayList<>());
        crawlerState3.setHistory(List.of(new CrawlerHistory(crawlerState3, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState inactiveCrawlerState1 = new CrawlerState("crawler1", StateType.INACTIVE, new ArrayList<>());
        inactiveCrawlerState1.setHistory(List.of(new CrawlerHistory(inactiveCrawlerState1, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState inactiveCrawlerState2 = new CrawlerState("crawler2", StateType.INACTIVE, new ArrayList<>());
        inactiveCrawlerState2.setHistory(List.of(new CrawlerHistory(inactiveCrawlerState2, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState inactiveCrawlerState3 = new CrawlerState("crawler3", StateType.INACTIVE, new ArrayList<>());
        inactiveCrawlerState3.setHistory(List.of(new CrawlerHistory(inactiveCrawlerState3, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));
        List<CrawlerState> activeCrawlers = List.of(crawlerState1, crawlerState2, crawlerState3);
        List<CrawlerState> inactiveCrawlers = List.of(inactiveCrawlerState1, inactiveCrawlerState2, inactiveCrawlerState3);

        List<Message> messagesCrawler1 = List.of(new Message().withMessageId(UUID.randomUUID().toString()).withBody("https://vk.com"));
        List<Message> messagesCrawler2 = List.of(new Message().withMessageId(UUID.randomUUID().toString()).withBody("https://ya.ru"));
        List<Message> messagesCrawler3 = List.of(new Message().withMessageId(UUID.randomUUID().toString()).withBody("https://google.com"));
        when(crawlerStateRepository.findAllByStatus(StateType.ACTIVE)).thenReturn(activeCrawlers).thenReturn(inactiveCrawlers).thenReturn(new ArrayList<>());
        when(messageQueue.getMessagesByGroupId("crawler1")).thenReturn(messagesCrawler1).thenReturn(new ArrayList<>());
        when(messageQueue.getMessagesByGroupId("crawler2")).thenReturn(messagesCrawler2).thenReturn(new ArrayList<>());
        when(messageQueue.getMessagesByGroupId("crawler3")).thenReturn(messagesCrawler3).thenReturn(new ArrayList<>());
        when(crawlerHistoryRepository.findFirstByCrawlerStateAndStatus(crawlerState1, HistoryStatus.IN_PROGRESS)).thenReturn(Optional.of(new CrawlerHistory(crawlerState1, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));
        when(crawlerHistoryRepository.findFirstByCrawlerStateAndStatus(crawlerState2, HistoryStatus.IN_PROGRESS)).thenReturn(Optional.of(new CrawlerHistory(crawlerState2, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));
        when(crawlerHistoryRepository.findFirstByCrawlerStateAndStatus(crawlerState3, HistoryStatus.IN_PROGRESS)).thenReturn(Optional.of(new CrawlerHistory(crawlerState3, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));
        crawlerProcessor.processTasks();

        verify(crawlerStateRepository, times(3)).save(any());
    }

    @Test
    public void testProcessTask() {
        CrawlerState crawlerState1 = new CrawlerState("crawler1", StateType.ACTIVE, new ArrayList<>());
        crawlerState1.setHistory(List.of(new CrawlerHistory(crawlerState1, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState crawlerState2 = new CrawlerState("crawler2", StateType.ACTIVE, new ArrayList<>());
        crawlerState2.setHistory(List.of(new CrawlerHistory(crawlerState2, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState crawlerState3 = new CrawlerState("crawler3", StateType.ACTIVE, new ArrayList<>());
        crawlerState3.setHistory(List.of(new CrawlerHistory(crawlerState3, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState inactiveCrawlerState1 = new CrawlerState("crawler1", StateType.INACTIVE, new ArrayList<>());
        inactiveCrawlerState1.setHistory(List.of(new CrawlerHistory(inactiveCrawlerState1, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState inactiveCrawlerState2 = new CrawlerState("crawler2", StateType.INACTIVE, new ArrayList<>());
        inactiveCrawlerState2.setHistory(List.of(new CrawlerHistory(inactiveCrawlerState2, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        CrawlerState inactiveCrawlerState3 = new CrawlerState("crawler3", StateType.INACTIVE, new ArrayList<>());
        inactiveCrawlerState3.setHistory(List.of(new CrawlerHistory(inactiveCrawlerState3, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));
        List<CrawlerState> activeCrawlers = List.of(crawlerState1, crawlerState2, crawlerState3);
        List<CrawlerState> inactiveCrawlers = List.of(inactiveCrawlerState1, inactiveCrawlerState2, inactiveCrawlerState3);

        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);

        when(message1.withMessageId(anyString())).thenReturn(message1);
        when(message1.withBody(anyString())).thenReturn(message1);
        when(message2.withMessageId(anyString())).thenReturn(message2);
        when(message2.withBody(anyString())).thenReturn(message2);

        List<Message> messagesCrawler1 = List.of(message1.withMessageId(UUID.randomUUID().toString()).withBody("https://vk.com"));
        List<Message> messagesCrawler2 = List.of(message2.withMessageId(UUID.randomUUID().toString()).withBody("https://ya.ru"));
        List<Message> messagesCrawler3 = List.of(new Message().withMessageId(UUID.randomUUID().toString()).withBody("https://google.com"));

        when(crawlerStateRepository.findAllByStatus(StateType.ACTIVE)).thenReturn(activeCrawlers).thenReturn(inactiveCrawlers).thenReturn(new ArrayList<>());
        when(messageQueue.getMessagesByGroupId("crawler1")).thenReturn(messagesCrawler1).thenReturn(new ArrayList<>());
        when(messageQueue.getMessagesByGroupId("crawler2")).thenReturn(messagesCrawler2).thenReturn(new ArrayList<>());
        when(messageQueue.getMessagesByGroupId("crawler3")).thenReturn(messagesCrawler3).thenReturn(new ArrayList<>());
        when(crawlerHistoryRepository.findFirstByCrawlerStateAndStatus(crawlerState1, HistoryStatus.IN_PROGRESS)).thenReturn(Optional.of(new CrawlerHistory(crawlerState1, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));
        when(crawlerHistoryRepository.findFirstByCrawlerStateAndStatus(crawlerState2, HistoryStatus.IN_PROGRESS)).thenReturn(Optional.of(new CrawlerHistory(crawlerState2, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));
        when(crawlerHistoryRepository.findFirstByCrawlerStateAndStatus(crawlerState3, HistoryStatus.IN_PROGRESS)).thenReturn(Optional.of(new CrawlerHistory(crawlerState3, LocalDateTime.now(), 1, HistoryStatus.IN_PROGRESS)));

        when(messagesCrawler1.get(0).getBody()).thenReturn("https://vk.com");
        when(messagesCrawler2.get(0).getBody()).thenReturn("https://ya.ru");

        assertDoesNotThrow(() -> crawlerProcessor.processTasks());
    }
}