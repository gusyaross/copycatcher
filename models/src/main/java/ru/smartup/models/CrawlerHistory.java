package ru.smartup.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "crawler_history")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CrawlerHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crawler_history_id_generator")
    @SequenceGenerator(name = "crawler_history_id_generator", sequenceName = "crawler_history_id_sequence", allocationSize = 1)
    private long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crawler_state_id")
    private CrawlerState crawlerState;

    @NonNull
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NonNull
    private int totalPages;

    private int pagesSkipped;

    private int pagesFailed;

    private int pagesFetched;

    @NonNull
    @Enumerated(EnumType.STRING)
    private HistoryStatus status;

    public void addPagesFetched(int pagesFetched) {
        this.pagesFetched += pagesFetched;
    }

    public void addPagesFailed(int pagesFailed) { this.pagesFailed += pagesFailed; }

    public void addPagesSkipped(int pagesSkipped) {
        this.pagesSkipped += pagesSkipped;
    }
}
