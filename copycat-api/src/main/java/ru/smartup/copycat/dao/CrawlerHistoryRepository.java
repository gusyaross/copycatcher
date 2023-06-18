package ru.smartup.copycat.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.smartup.models.CrawlerHistory;
import ru.smartup.models.CrawlerState;

import java.util.Optional;

@Repository
public interface CrawlerHistoryRepository extends CrudRepository<CrawlerHistory, Long> {
    Optional<CrawlerHistory> findTopByCrawlerStateOrderByStartTimeDesc(CrawlerState state);
}
