CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events
(
    id                       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    category_id              BIGINT                                                NOT NULL,
    event_date               timestamp                                             NOT NULL,
    title                    VARCHAR(120)                                          NOT NULL,
    annotation               VARCHAR(2000)                                         NOT NULL,
    description              VARCHAR(7000)                                         NOT NULL,
    location_lat             DOUBLE PRECISION                                      NOT NULL,
    location_lon             DOUBLE PRECISION                                      NOT NULL,
    initiator_id             BIGINT                                                NOT NULL,
    participant_limit        INTEGER                     DEFAULT 0                 NOT NULL,
    is_payment_required      BOOLEAN                     DEFAULT FALSE             NOT NULL,
    request_moderation       BOOLEAN                     DEFAULT TRUE              NOT NULL,
    confirmed_requests_count INTEGER                     DEFAULT 0                 NOT NULL,
    state                    VARCHAR(15)                                           NOT NULL,
    published_on             TIMESTAMP WITHOUT TIME ZONE,
    created_on               TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_events_categories FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_events_users FOREIGN KEY (initiator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title     VARCHAR(50)           NOT NULL,
    is_pinned BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events
(
    compilation_id BIGINT not null,
    event_id       BIGINT not null,
    CONSTRAINT pk_event_compilations PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT fk_event_compilations_compilations FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT fk_event_compilations_events FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS participation_requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id     BIGINT                                                NOT NULL,
    requester_id BIGINT                                                NOT NULL,
    status       VARCHAR(15)                                           NOT NULL,
    created_on   TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_requests_events FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_requests_users FOREIGN KEY (requester_id) REFERENCES users (id),
    UNIQUE (event_id, requester_id)
);