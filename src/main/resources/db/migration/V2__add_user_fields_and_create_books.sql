-- 1. Expandir la tabla de usuarios
ALTER TABLE users ADD COLUMN gender VARCHAR(20) DEFAULT 'N/R';
ALTER TABLE users ADD COLUMN birth_date DATE;

-- 2. Crear la tabla de libros (HU-F02-01)
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    category VARCHAR(100),
    language VARCHAR(10) DEFAULT 'es',
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, DAMAGED
    cover_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);