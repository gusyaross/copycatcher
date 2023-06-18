package ru.smartup.crawler.tasks;

import com.amazonaws.services.sqs.model.Message;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import ru.smartup.crawler.dao.CrawlerHistoryRepository;
import ru.smartup.crawler.dao.CrawlerStateRepository;
import ru.smartup.crawler.dao.PageRepository;
import ru.smartup.models.CrawlerHistory;
import ru.smartup.models.CrawlerState;
import ru.smartup.models.HistoryStatus;
import ru.smartup.models.Page;
import ru.smartup.utils.MessageQueue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>The CrawlerMessageProcessingTask is a task that reads messages from message queue and saves history to database<p/>
 * */
@RequiredArgsConstructor
@Getter
public class CrawlerMessageProcessingTask implements Runnable {
    private final Logger LOGGER = LoggerFactory.getLogger(CrawlerMessageProcessingTask.class);
    @NonNull
    private CrawlerState crawlerState;
    @NonNull
    private List<Message> messages;
    @NonNull
    private CrawlerStateRepository crawlerStateRepository;
    @NonNull
    private CrawlerHistoryRepository crawlerHistoryRepository;
    @NonNull
    private PageRepository pageRepository;

    @NonNull
    private MessageQueue messageQueue;

    @NonNull
    private CloseableHttpClient httpClient;


    /**
     * This method splits each message messages list to get list of URLs. Then parsing text from url and save it in db. After that method calls repository to save messages process results*/
    @Override
    public void run() {
        for (Message message : messages) {
            List<Page> pages = new ArrayList<>();
            int pagesFailed = 0;
            int pagesFetched = 0;
            int pagesSkipped = 0;
            List<String> urls = List.of(message.getBody().split(","));
            LOGGER.info(String.format("Crawler %s: reading tasks %s", crawlerState.getCrawlerName(), urls));
            for (String url : urls) {
                if (pageRepository.findByUrl(url) != null) {
                    LOGGER.info(String.format("Text from url %s already saved in database", url));
                } else {
                    HttpGet httpGet = new HttpGet(url);
                    try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                        HttpEntity entity = httpResponse.getEntity();
                        ContentHandler handler = new BodyContentHandler();
                        Parser parser = new HtmlParser();
                        parser.parse(entity.getContent(), handler, new Metadata(), new ParseContext());
                        pages.add(new Page(url, handler.toString()));
                    } catch (IOException e) {
                        LOGGER.error(String.format("Error in http get from %s", url));
                        pagesFailed++;
                    } catch (TikaException | SAXException e) {
                        LOGGER.error(String.format("Error in parsing from %s", url));
                        pagesFailed++;
                    }
                }
                pagesFetched++;
            }
            pageRepository.saveAll(pages);

            Optional<CrawlerHistory> crawlerHistoryOptional = crawlerHistoryRepository.findFirstByCrawlerStateAndStatus(crawlerState, HistoryStatus.IN_PROGRESS);
            if (crawlerHistoryOptional.isPresent()) {
                CrawlerHistory crawlerHistory = crawlerHistoryOptional.get();
                crawlerHistory.addPagesFetched(pagesFetched);
                crawlerHistory.addPagesFailed(pagesFailed);
                crawlerHistory.addPagesSkipped(pagesSkipped);

                crawlerHistory.setEndTime(LocalDateTime.now());
                crawlerHistory.setStatus(HistoryStatus.FINISHED);

                crawlerHistoryRepository.save(crawlerHistory);
                LOGGER.info(String.format("Crawler '%s' was finished", crawlerState.getCrawlerName()));
            } else {
                LOGGER.error(String.format("Cannot update crawler '%s' history", crawlerState.getCrawlerName()));
            }
            messageQueue.deleteMessage(message.getReceiptHandle());
        }
    }
}