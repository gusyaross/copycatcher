package ru.smartup.crawler.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.smartup.models.CrawlerHistory;
import ru.smartup.models.CrawlerState;
import ru.smartup.models.HistoryStatus;

import java.util.Optional;

@Repository
public interface CrawlerHistoryRepository extends CrudRepository<CrawlerHistory, Long> {
    Optional<CrawlerHistory> findFirstByCrawlerStateAndStatus(CrawlerState crawlerState, HistoryStatus status);
}
