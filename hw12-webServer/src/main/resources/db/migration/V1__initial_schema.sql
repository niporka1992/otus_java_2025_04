-- ==============================
-- 1. Последовательности
-- ==============================
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE clients_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE addresses_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE phones_seq START WITH 1 INCREMENT BY 1;

-- ==============================
-- 2. Таблица пользователей
-- ==============================
CREATE TABLE users (
                       id       BIGINT PRIMARY KEY DEFAULT nextval('users_seq'),
                       name     VARCHAR(255) NOT NULL,
                       login    VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

-- ==============================
-- 3. Таблица клиентов
-- ==============================
CREATE TABLE clients (
                         id   BIGINT PRIMARY KEY DEFAULT nextval('clients_seq'),
                         name VARCHAR(255) NOT NULL
);

-- ==============================
-- 4. Таблица адресов
-- ==============================
CREATE TABLE addresses (
                           id        BIGINT PRIMARY KEY DEFAULT nextval('addresses_seq'),
                           street    VARCHAR(255) NOT NULL,
                           client_id BIGINT UNIQUE,
                           CONSTRAINT fk_address_client FOREIGN KEY (client_id)
                               REFERENCES clients (id) ON DELETE CASCADE
);

-- ==============================
-- 5. Таблица телефонов
-- ==============================
CREATE TABLE phones (
                        id        BIGINT PRIMARY KEY DEFAULT nextval('phones_seq'),
                        number    VARCHAR(255) NOT NULL,
                        client_id BIGINT,
                        CONSTRAINT fk_phone_client FOREIGN KEY (client_id)
                            REFERENCES clients (id) ON DELETE CASCADE
);

-- ==============================
-- 6. Индексы
-- ==============================
CREATE INDEX idx_user_login ON users (login);
CREATE INDEX idx_phone_client_id ON phones (client_id);
CREATE INDEX idx_address_client_id ON addresses (client_id);
