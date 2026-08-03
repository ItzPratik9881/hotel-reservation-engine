CREATE TABLE rooms (

    id BIGSERIAL PRIMARY KEY,

    room_number VARCHAR(50) NOT NULL,

    room_type VARCHAR(50) NOT NULL,

    capacity INTEGER NOT NULL,

    price_per_night NUMERIC(10,2) NOT NULL,

    available BOOLEAN NOT NULL DEFAULT TRUE,

    hotel_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_room_hotel
        FOREIGN KEY (hotel_id)
        REFERENCES hotels(id)
        ON DELETE CASCADE
);