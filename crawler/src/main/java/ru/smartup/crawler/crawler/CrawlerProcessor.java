package ru.smartup.crawler.crawler;

import com.amazonaws.services.sqs.model.Message;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.smartup.crawler.dao.CrawlerHistoryRepository;
import ru.smartup.crawler.dao.CrawlerStateRepository;
import ru.smartup.crawler.dao.PageRepository;
import ru.smartup.crawler.exceptions.UnableToProcessMessageException;
import ru.smartup.models.*;
import ru.smartup.crawler.tasks.CrawlerMessageProcessingTask;
import ru.smartup.utils.MessageQueue;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Crawler service is a service that provides message processing from message queue to read and analyse URLs from each message.
 * Service gets content and inner URLs form each URL to find similar contents from other URLs*/
@Service
public class CrawlerProcessor {
    private final Logger LOGGER = LoggerFactory.getLogger(CrawlerProcessor.class);
    private final CrawlerStateRepository crawlerStateRepository;
    private final CrawlerHistoryRepository crawlerHistoryRepository;
    private final PageRepository pageRepository;
    private final MessageQueue messageQueue;
    private final ThreadPoolExecutor threadPool;
    private final CloseableHttpClient httpClient;


    public CrawlerProcessor(CrawlerStateRepository crawlerStateRepository, CrawlerHistoryRepository crawlerHistoryRepository, PageRepository pageRepository, MessageQueue messageQueue, ThreadPoolExecutor threadPool, CloseableHttpClient httpClient) {
        this.crawlerStateRepository = crawlerStateRepository;
        this.crawlerHistoryRepository = crawlerHistoryRepository;
        this.pageRepository = pageRepository;
        this.messageQueue = messageQueue;
        this.threadPool = threadPool;
        this.httpClient = httpClient;
    }

    /**readTasks is a method which reads from a series of messages, parallelize messages across active crawlers, and then saves a progress report to the database*/
    @Scheduled(fixedRate = 60000)
    public void processTasks() {
        try {
            LOGGER.info("Crawler app has started reading tasks");
            List<CrawlerState> activeCrawlers;
            do {
                activeCrawlers = crawlerStateRepository.findAllByStatus(StateType.ACTIVE);
                for (CrawlerState crawlerState : activeCrawlers) {
                    List<Message> messages = messageQueue.getMessagesByGroupId(crawlerState.getCrawlerName());
                    if (messages.isEmpty()) {
                        crawlerState.setStatus(StateType.INACTIVE);
                        crawlerStateRepository.save(crawlerState);
                    }
                    try {
                        threadPool.execute(new CrawlerMessageProcessingTask(crawlerState, messages, crawlerStateRepository, crawlerHistoryRepository, pageRepository, messageQueue, httpClient));
                    } catch (RejectedExecutionException ex) {
                        throw new UnableToProcessMessageException(ex);
                    }
                }
            } while (!activeCrawlers.isEmpty());
            LOGGER.info("Crawler app has finished reading tasks");
        } catch (Throwable ex) {
            LOGGER.error(ex.toString());
        }
    }
}
