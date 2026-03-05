ALTER TABLE reservations
    ADD COLUMN id_card_photo_url VARCHAR(500) NULL,
  ADD COLUMN face_photo_url VARCHAR(500) NULL,
  ADD COLUMN verified_at DATETIME NULL,
  ADD COLUMN verified_by_id BIGINT NULL,
  ADD COLUMN verify_note VARCHAR(500) NULL;

ALTER TABLE reservations
    ADD CONSTRAINT fk_reservations_verified_by
        FOREIGN KEY (verified_by_id) REFERENCES users(id);