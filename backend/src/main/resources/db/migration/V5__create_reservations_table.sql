CREATE TABLE reservations (

    id BIGSERIAL PRIMARY KEY,

    check_in_date DATE NOT NULL,

    check_out_date DATE NOT NULL,

    number_of_guests INTEGER NOT NULL,

    total_price NUMERIC(10,2) NOT NULL,

    booking_status VARCHAR(30) NOT NULL,

    room_id BIGINT NOT NULL,

    user_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_reservation_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reservation_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);