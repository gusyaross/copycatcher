package ru.smartup.copycat.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.smartup.models.CrawlerState;
import ru.smartup.models.StateType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrawlerStateRepository extends CrudRepository<CrawlerState, Long> {
    Optional<CrawlerState> findByCrawlerNameEquals(String crawlerName);
    List<CrawlerState> findAllByStatus(StateType status);
}
