CREATE TABLE user_favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_favorites_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_favorites_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT uq_user_favorite UNIQUE (user_id, book_id)
);

CREATE INDEX idx_user_favorites_user_id ON user_favorites(user_id);
CREATE INDEX idx_user_favorites_book_id ON user_favorites(book_id);
