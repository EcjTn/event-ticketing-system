CREATE TABLE ticket (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    event_id        BIGINT NOT NULL,
    tier            ticket_tier NOT NULL,
    price_paid      NUMERIC(10,2) NOT NULL,
    unique_code     VARCHAR(255) NOT NULL UNIQUE,
    status          ticket_status NOT NULL DEFAULT 'VALID',
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ticket_user_id ON ticket(user_id);
CREATE INDEX idx_ticket_event_id ON ticket(event_id);