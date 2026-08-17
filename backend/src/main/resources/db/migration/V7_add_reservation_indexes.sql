CREATE INDEX IF NOT EXISTS idx_reservations_room_dates
ON reservations (room_id, check_in_date, check_out_date);