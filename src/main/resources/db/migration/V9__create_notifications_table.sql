CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(40) NOT NULL,
    title VARCHAR(120) NOT NULL,
    message TEXT NOT NULL,
    target_view VARCHAR(40),
    target_id BIGINT,
    dedupe_key VARCHAR(160),
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_notifications_type CHECK (type IN (
        'LOAN_CREATED',
        'LOAN_RENEWED',
        'LOAN_RETURNED',
        'LOAN_DUE_SOON',
        'LOAN_OVERDUE',
        'RESERVATION_CREATED',
        'RESERVATION_CANCELLED',
        'RESERVATION_FULFILLED'
    ))
);

CREATE UNIQUE INDEX ux_notifications_dedupe_key
    ON notifications(dedupe_key)
    WHERE dedupe_key IS NOT NULL;

CREATE INDEX idx_notifications_user_read ON notifications(user_id, read_at);
CREATE INDEX idx_notifications_user_created ON notifications(user_id, created_at DESC);
