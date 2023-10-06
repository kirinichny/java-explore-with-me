CREATE TABLE IF NOT EXISTS endpoint_hits
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app       VARCHAR(100) NOT NULL,
    uri       VARCHAR(100) NOT NULL,
    ip        VARCHAR(15)  NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);