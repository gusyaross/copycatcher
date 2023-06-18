package ru.smartup.copycat.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.smartup.models.Page;

import java.util.List;

@Repository
public interface PageRepository extends CrudRepository<Page, Long> {
    List<Page> findByWasIndexedTrue();
    long countByWasIndexedTrue();
}
