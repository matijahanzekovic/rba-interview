
CREATE TABLE person_status (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL
);

CREATE TABLE person (
    id              SERIAL PRIMARY KEY,
    oib             CHAR(11) NOT NULL,
    first_name      VARCHAR(50) NOT NULL,
    last_name       VARCHAR(50) NOT NULL,
    status_id       BIGINT REFERENCES person_status(id)    ,
    UNIQUE(oib)
);

CREATE TABLE document_status (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL
);

CREATE TABLE document (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    location        VARCHAR(100) NOT NULL,
    person_id       BIGINT REFERENCES person(id),
    status_id       BIGINT REFERENCES document_status(id)
);