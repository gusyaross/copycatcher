package ru.smartup.copycat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.smartup.copycat.dto.response.GetIndexingStatusResponse;
import ru.smartup.copycat.dao.PageRepository;
import ru.smartup.copycat.services.PageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestPageService {
    private PageRepository pageRepository = mock(PageRepository.class);

    @MockBean
    private PageService pageService;

    @BeforeEach
    public void setUp() {
        pageService = new PageService(pageRepository);
    }

    @Test
    public void testIndexingStatus() {
        when(pageRepository.count()).thenReturn(1L);
        when(pageRepository.countByWasIndexedTrue()).thenReturn(1L);

        GetIndexingStatusResponse response = pageService.getIndexingStatus();

        assertEquals(response.getTotalPages(),1L);
        assertEquals(response.getIndexedPages(),1L);
    }
}
