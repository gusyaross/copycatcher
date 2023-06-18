package ru.smartup.copycat.services;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import ru.smartup.copycat.dto.response.GetIndexingStatusResponse;
import ru.smartup.copycat.dao.PageRepository;
import ru.smartup.copycat.dto.response.GetSimilarPagesResponse;
import ru.smartup.models.Page;
import ru.smartup.copycat.tasks.IndexingPagesTask;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     PageService is a service to handle user request with pages
 * </p>*/
@Service
public class PageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);
    private final PageRepository pageRepository;
    private final String indexPath = "index";

    public PageService(PageRepository pageRepository) { this.pageRepository = pageRepository; }

    /**
     * <p>
     *     startIndexingPages start of indexing pages from db where wasIndexing = false
     * </p>
     */
    public void startIndexingPages() {
        List<Page> pages = pageRepository.findByWasIndexedTrue();
        if (pages.isEmpty()) {
            LOGGER.info("Nothing to indexing");
        } else {
            LOGGER.info("Start of indexing texts from db");
            new Thread(() -> {
                new IndexingPagesTask(pages, indexPath).run();
                pageRepository.saveAll(pages);
            }).start();
        }
    }

    /**
     * <p>
     *     getIndexingStatus getting status indexing pages
     * </p>
     *
     * @return Response with total pages and pages which was indexed */
    public GetIndexingStatusResponse getIndexingStatus() {
        long totalPages = pageRepository.count();
        long indexedPages = pageRepository.countByWasIndexedTrue();
        LOGGER.info("Indexing status successfully got");

        return new GetIndexingStatusResponse(totalPages, indexedPages);
    }

    public GetSimilarPagesResponse getSimilarPages(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
            HttpEntity entity = httpResponse.getEntity();
            ContentHandler handler = new BodyContentHandler();
            Parser parser = new HtmlParser();
            parser.parse(entity.getContent(), handler, new Metadata(), new ParseContext());
            Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            MoreLikeThis mlt = new MoreLikeThis(indexReader);
            mlt.setMinTermFreq(1);
            mlt.setMinDocFreq(1);
            Query query = mlt.like("text",new StringReader(handler.toString()));
            TopDocs similarDocs = indexSearcher.search(query, 5);
            Map<String,Float> urlToScore = new LinkedHashMap<>();
            for (ScoreDoc scoreDoc : similarDocs.scoreDocs) {
                int docId = scoreDoc.doc;
                Document similarDoc = indexSearcher.doc(docId);
                urlToScore.put(similarDoc.get("url"),scoreDoc.score);
            }
            indexDirectory.close();
            indexReader.close();
            return new GetSimilarPagesResponse(urlToScore);
        } catch (IOException e) {
            LOGGER.error(String.format("Error in http get from %s", url));
        } catch (TikaException | SAXException e) {
            LOGGER.error(String.format("Error in parsing from %s", url));
        }
        return null;
    }
}
