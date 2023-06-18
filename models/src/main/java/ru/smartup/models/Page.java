package ru.smartup.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "page")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_id_generator")
    @SequenceGenerator(name = "page_id_generator", sequenceName = "page_id_sequence", allocationSize = 1)
    private long id;

    @NonNull
    private String url;

    @NonNull
    private String text;

    private boolean wasIndexed;
}