CREATE TABLE payment (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT NOT NULL UNIQUE,
    user_id         BIGINT NOT NULL,
    event_id        BIGINT NOT NULL,
    amount          NUMERIC(10,2) NOT NULL,
    method          payment_method NOT NULL DEFAULT 'MOCK',
    status          payment_status NOT NULL,
    paid_at         TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_payment_user_id ON payment(user_id);
CREATE INDEX idx_payment_event_id ON payment(event_id);
CREATE INDEX idx_payment_order_id ON payment(order_id);