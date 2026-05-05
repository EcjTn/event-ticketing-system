CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        user_role NOT NULL DEFAULT 'CUSTOMER',
    profile_image_url VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);