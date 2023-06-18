package ru.smartup.copycat.tasks;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.smartup.models.Page;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class IndexingPagesTask implements Runnable {
    private final Logger LOGGER = LoggerFactory.getLogger(IndexingPagesTask.class);
    private final List<Page> pages;
    private final String indexPath;

    public IndexingPagesTask(List<Page> pages, String indexPath) {
        this.pages = pages;
        this.indexPath = indexPath;
    }

    @Override
    public void run() {
        try {
            Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
            IndexWriterConfig writerConfig = new IndexWriterConfig(new StandardAnalyzer());
            IndexWriter indexWriter = new IndexWriter(indexDirectory, writerConfig);
            for (Page page : pages) {
                Document doc = new Document();
                Field field1 = new TextField("url", page.getUrl(), Field.Store.YES);
                Field field2 = new TextField("text", page.getText(), Field.Store.YES);
                doc.add(field1);
                doc.add(field2);
                indexWriter.addDocument(doc);

                page.setWasIndexed(true);
            }

            indexWriter.commit();
            indexDirectory.close();
            indexWriter.close();
        } catch (IOException e) {
            LOGGER.error("indexing error");
        }
        LOGGER.info("End of indexing texts from db");
    }
}
