CREATE TABLE events (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    date        TIMESTAMP NOT NULL,
    venue       VARCHAR(255) NOT NULL,
    description TEXT,
    image_url   VARCHAR(500),
    status      event_status NOT NULL DEFAULT 'DRAFT',
    created_by  BIGINT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE event_tier (
    id          BIGSERIAL PRIMARY KEY,
    event_id    BIGINT NOT NULL REFERENCES events(id),
    tier ticket_tier NOT NULL,
    price       NUMERIC(10,2) NOT NULL,
    quantity    INT NOT NULL,
    sold_count  INT NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_event_tier_event_id_tier ON event_tier(event_id, tier);