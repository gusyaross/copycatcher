CREATE TABLE page (
    id BIGINT NOT NULL PRIMARY KEY,
    url TEXT NOT NULL,
    text TEXT NOT NULL,
     was_indexed BOOLEAN DEFAULT FALSE
);

CREATE UNIQUE INDEX url_idx ON page(url);

CREATE SEQUENCE page_id_sequence AS BIGINT;