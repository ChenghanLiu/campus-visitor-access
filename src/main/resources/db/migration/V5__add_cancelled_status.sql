ALTER TABLE reservations
DROP CHECK chk_reservations_status;

ALTER TABLE reservations
ADD CONSTRAINT chk_reservations_status
CHECK (status IN ('PENDING','APPROVED','REJECTED','CANCELLED'));
