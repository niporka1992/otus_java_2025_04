-- ==============================
-- 1. Последовательности
-- ==============================
CREATE SEQUENCE clients_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE addresses_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE phones_seq START WITH 1 INCREMENT BY 1;

-- ==============================
-- 2. Таблица клиентов
-- ==============================
CREATE TABLE clients (
                         id   BIGINT PRIMARY KEY DEFAULT nextval('clients_seq'),
                         name VARCHAR(255) NOT NULL
);

-- ==============================
-- 3. Таблица адресов
-- ==============================
CREATE TABLE addresses (
                           id        BIGINT PRIMARY KEY DEFAULT nextval('addresses_seq'),
                           street    VARCHAR(255) NOT NULL,
                           client_id BIGINT UNIQUE,
                           CONSTRAINT fk_address_client FOREIGN KEY (client_id)
                               REFERENCES clients (id) ON DELETE CASCADE
);

-- ==============================
-- 4. Таблица телефонов
-- ==============================
CREATE TABLE phones (
                        id           BIGINT PRIMARY KEY DEFAULT nextval('phones_seq'),
                        number       VARCHAR(255) NOT NULL,
                        client_id    BIGINT,
                        clients_key  INT NOT NULL,  -- 👈 ДОБАВИЛ КОЛОНКУ ПОРЯДКА
                        CONSTRAINT fk_phone_client FOREIGN KEY (client_id)
                            REFERENCES clients (id) ON DELETE CASCADE
);

-- ==============================
-- 5. Индексы
-- ==============================
CREATE INDEX idx_phone_client_id ON phones (client_id);
CREATE INDEX idx_phone_clients_key ON phones (clients_key);
CREATE INDEX idx_address_client_id ON addresses (client_id);
