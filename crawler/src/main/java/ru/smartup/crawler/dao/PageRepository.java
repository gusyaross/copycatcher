package ru.smartup.crawler.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.smartup.models.Page;

@Repository
public interface PageRepository extends CrudRepository<Page, Long> {
    Page findByUrl(String url);
}
