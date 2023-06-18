package ru.smartup.crawler.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.smartup.models.CrawlerState;
import ru.smartup.models.StateType;

import java.util.List;

@Repository
public interface CrawlerStateRepository extends CrudRepository<CrawlerState, Long> {
    List<CrawlerState> findAllByStatus(StateType status);
}
