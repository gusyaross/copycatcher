CREATE TABLE crawler_states (
    id BIGINT NOT NULL PRIMARY KEY,
    crawler_name TEXT NOT NULL,
    status VARCHAR(8) NOT NULL
);

CREATE TABLE crawler_history (
    id BIGINT NOT NULL PRIMARY KEY,
    crawler_state_id BIGINT NOT NULL REFERENCES crawler_states(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    total_pages INT,
    pages_skipped INT,
    pages_failed INT,
    pages_fetched INT,
    status VARCHAR(11) NOT NULL DEFAULT 'IN_PROGRESS'
);

CREATE UNIQUE INDEX crawler_name_idx ON crawler_states(crawler_name);

CREATE SEQUENCE crawler_states_id_sequence AS BIGINT;

CREATE SEQUENCE crawler_history_id_sequence AS BIGINT;