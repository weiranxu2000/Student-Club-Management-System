BEGIN;
-- DROP SCHEMA app CASCADE;
CREATE SCHEMA IF NOT EXISTS app AUTHORIZATION student_club_database_manager;

-- CREATE TYPE STATUS AS ENUM ('created', 'cancelled');
-- CREATE TYPE FUND_STATUS AS ENUM ('in-draft', 'submitted', 'in-review', 'approved', 'rejected');

CREATE TABLE IF NOT EXISTS app.user
(
    e_mail     VARCHAR(255) NOT NULL,
    student_id INTEGER UNIQUE,
    admin_id   INTEGER UNIQUE,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    PRIMARY KEY (e_mail)
);

CREATE TABLE IF NOT EXISTS app.club
(
    id          INTEGER      NOT NULL,
    name        VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS app.student_club
(
    student_id INTEGER NOT NULL,
    club_id    INTEGER NOT NULL,
    is_admin   BOOLEAN NOT NULL,
    PRIMARY KEY (student_id, club_id),
    FOREIGN KEY (student_id) REFERENCES app.user (student_id),
    FOREIGN KEY (club_id) REFERENCES app.club (id)
);

CREATE TABLE IF NOT EXISTS app.venue
(
    id       INTEGER      NOT NULL,
    name     VARCHAR(255) UNIQUE NOT NULL,
    capacity INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS app.event
(
    id          uuid         NOT NULL UNIQUE,
    club_id     INTEGER      NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    venue_id    INTEGER      NOT NULL,
    date        DATE         NOT NULL,
    time        TIME         NOT NULL,
    cost        INTEGER      NOT NULL,
    status      VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (club_id) REFERENCES app.club (id),
    FOREIGN KEY (venue_id) REFERENCES app.venue (id),
    -- There can not be two events in the same spot at the same time.
    UNIQUE (venue_id, date, time)
);

CREATE TABLE IF NOT EXISTS app.rsvp
(
    id         uuid    NOT NULL UNIQUE,
    student_id INTEGER NOT NULL,
    event_id   uuid    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (student_id) REFERENCES app.user (student_id),
    FOREIGN KEY (event_id) REFERENCES app.event (id),
    -- UNIQUE (student_id, event_id)
);

CREATE TABLE IF NOT EXISTS app.ticket
(
    -- id         uuid         NOT NULL UNIQUE,
    rsvp_id    uuid         NOT NULL,
    student_id INTEGER      NOT NULL,
    status     VARCHAR(255) NOT NULL,
    PRIMARY KEY (rsvp_id, student_id),
    FOREIGN KEY (rsvp_id) REFERENCES app.rsvp (id),
    FOREIGN KEY (student_id) REFERENCES app.user (student_id)
);

CREATE TABLE IF NOT EXISTS app.fund
(
    id          uuid UNIQUE              NOT NULL,
    description VARCHAR(500)             NOT NULL,
    amount      INTEGER                  NOT NULL,
    time        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    club_id     INTEGER                  NOT NULL,
    status      VARCHAR(255)             NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (club_id) REFERENCES app.club (id)
);


COMMIT;