CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(320),
    name  varchar(250),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  varchar(50),
    CONSTRAINT UQ_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat  NUMERIC,
    lon NUMERIC,
    UNIQUE (lat, lon)
);

CREATE TABLE IF NOT EXISTS events
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation  varchar(2000),
    category_id  BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    published TIMESTAMP WITHOUT TIME ZONE,
    description varchar(7000),
    location_id BIGINT,
    paid BOOLEAN,
    participant_limit BIGINT,
    request_moderation BOOLEAN,
    title varchar(120),
    state varchar(30),
    user_id BIGINT,
    CONSTRAINT fk_event_to_cat FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_event_to_loc FOREIGN KEY (location_id) REFERENCES locations (id),
    CONSTRAINT fk_event_to_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id  BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    status varchar(30),
    user_id BIGINT,
    CONSTRAINT fk_request_to_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_reques_to_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN,
    title  varchar(50)
);
CREATE TABLE IF NOT EXISTS Compilation_event
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id  BIGINT,
    comp_ip BIGINT,
    CONSTRAINT fk_to_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_to_comp FOREIGN KEY (comp_ip) REFERENCES compilations (id)
);
CREATE TABLE IF NOT EXISTS comments
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id  BIGINT,
    event_id BIGINT,
    published TIMESTAMP WITHOUT TIME ZONE,
    comment  varchar(2000),
    CONSTRAINT fk_to_ev FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_to_com FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS response
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id  BIGINT,
    comment_id BIGINT,
    published TIMESTAMP WITHOUT TIME ZONE,
    response  varchar(2000),
    CONSTRAINT fk_to_resp FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_to_comment FOREIGN KEY (comment_id) REFERENCES comments (id)
);
