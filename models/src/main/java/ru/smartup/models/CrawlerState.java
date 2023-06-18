package ru.smartup.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "crawler_states")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CrawlerState {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crawler_states_id_generator")
    @SequenceGenerator(name = "crawler_states_id_generator", sequenceName = "crawler_states_id_sequence", allocationSize = 1)
    private long id;

    @NonNull
    private String crawlerName;

    @Enumerated(EnumType.STRING)
    @NonNull
    private StateType status;

    @OneToMany(mappedBy = "crawlerState", orphanRemoval = true, cascade = CascadeType.ALL)
    @NonNull
    private List<CrawlerHistory> history;

    public void addHistory(CrawlerHistory crawlerHistory) {
        history.add(crawlerHistory);
    }
}