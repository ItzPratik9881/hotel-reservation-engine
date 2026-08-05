CREATE TABLE payments (

    id BIGSERIAL PRIMARY KEY,

    reservation_id BIGINT NOT NULL,

    amount NUMERIC(10,2) NOT NULL,

    payment_method VARCHAR(30) NOT NULL,

    payment_status VARCHAR(30) NOT NULL,

    transaction_id VARCHAR(255) UNIQUE NOT NULL,

    paid_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_payment_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservations(id)
        ON DELETE CASCADE
);