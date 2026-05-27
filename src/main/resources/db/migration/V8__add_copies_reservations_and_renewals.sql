CREATE TABLE book_copies (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    copy_code VARCHAR(60) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_book_copies_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT chk_book_copies_status CHECK (status IN ('AVAILABLE', 'LOANED', 'RESERVED', 'INACTIVE'))
);

INSERT INTO book_copies (book_id, copy_code, status)
SELECT
    b.id,
    CONCAT('BOOK-', b.id, '-COPY-1'),
    CASE WHEN UPPER(COALESCE(b.status, 'ACTIVE')) = 'ACTIVE' THEN 'AVAILABLE' ELSE 'INACTIVE' END
FROM books b;

ALTER TABLE loans ADD COLUMN copy_id BIGINT;
ALTER TABLE loans ADD COLUMN due_date TIMESTAMP;
ALTER TABLE loans ADD COLUMN renewal_count INTEGER NOT NULL DEFAULT 0;

UPDATE loans
SET due_date = COALESCE(loan_date, created_at, CURRENT_TIMESTAMP) + INTERVAL '7 days'
WHERE due_date IS NULL;

UPDATE loans l
SET copy_id = c.id
FROM book_copies c
WHERE l.book_id = c.book_id
  AND UPPER(l.status) IN ('ACTIVE', 'OVERDUE')
  AND l.copy_id IS NULL;

UPDATE book_copies c
SET status = 'LOANED'
WHERE EXISTS (
    SELECT 1
    FROM loans l
    WHERE l.copy_id = c.id
      AND UPPER(l.status) IN ('ACTIVE', 'OVERDUE')
);

ALTER TABLE loans ALTER COLUMN due_date SET NOT NULL;
ALTER TABLE loans ADD CONSTRAINT fk_loans_copy FOREIGN KEY (copy_id) REFERENCES book_copies(id);
ALTER TABLE loans ADD CONSTRAINT chk_loans_status CHECK (status IN ('ACTIVE', 'OVERDUE', 'RETURNED'));
ALTER TABLE loans ADD CONSTRAINT chk_loans_renewal_count CHECK (renewal_count >= 0);

CREATE UNIQUE INDEX ux_loans_open_copy
    ON loans(copy_id)
    WHERE copy_id IS NOT NULL AND status IN ('ACTIVE', 'OVERDUE');

CREATE TABLE loan_renewals (
    id BIGSERIAL PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    previous_due_date TIMESTAMP NOT NULL,
    new_due_date TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loan_renewals_loan FOREIGN KEY (loan_id) REFERENCES loans(id),
    CONSTRAINT chk_loan_renewals_duration CHECK (duration_minutes BETWEEN 5 AND 10080)
);

CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    fulfilled_loan_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    requested_loan_duration_minutes INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fulfilled_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    CONSTRAINT fk_reservations_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reservations_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_reservations_fulfilled_loan FOREIGN KEY (fulfilled_loan_id) REFERENCES loans(id),
    CONSTRAINT chk_reservations_status CHECK (status IN ('WAITING', 'FULFILLED', 'CANCELLED')),
    CONSTRAINT chk_reservations_duration CHECK (requested_loan_duration_minutes BETWEEN 5 AND 10080)
);

CREATE UNIQUE INDEX ux_reservations_waiting_user_book
    ON reservations(user_id, book_id)
    WHERE status = 'WAITING';

CREATE INDEX idx_book_copies_book_id ON book_copies(book_id);
CREATE INDEX idx_book_copies_status ON book_copies(status);
CREATE INDEX idx_loans_copy_id ON loans(copy_id);
CREATE INDEX idx_loans_due_date ON loans(due_date);
CREATE INDEX idx_reservations_user_id ON reservations(user_id);
CREATE INDEX idx_reservations_book_id ON reservations(book_id);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_loan_renewals_loan_id ON loan_renewals(loan_id);
