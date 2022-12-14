CREATE TABLE IF NOT EXISTS USERS
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    requester_id BIGINT,
    CONSTRAINT pk_item_request PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_REQUEST_ON_REQUESTER FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS ITEMS
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR UNIQUE NOT NULL,
    description VARCHAR,
    available   BOOLEAN,
    owner_id    BIGINT NOT NULL,
    request_id  BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT FK_ITEM_ON_REQUEST FOREIGN KEY (request_id) REFERENCES requests (id),
    CONSTRAINT UQ_OWNER_ITEM_NAME UNIQUE(owner_id, name)
);

CREATE TABLE IF NOT EXISTS BOOKINGS
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    booker_id   BIGINT,
    item_id     BIGINT,
    status      VARCHAR,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT FK_BOOKING_ON_BOOKER FOREIGN KEY (booker_id) REFERENCES users (id),
    CONSTRAINT FK_BOOKING_ON_ITEM FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text        VARCHAR NOT NULL,
    item_id     BIGINT,
    author_id   BIGINT,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT FK_COMMENT_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT FK_COMMENT_ON_ITEM FOREIGN KEY (item_id) REFERENCES items (id)
);
