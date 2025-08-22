-- ==============================
-- 1. Администратор
-- ==============================
INSERT INTO users (id, name, login, password, is_admin)
VALUES (nextval('users_seq'), 'Администратор', 'Admin', '11111', TRUE);

-- ==============================
-- 2. Клиенты
-- ==============================
INSERT INTO clients (id, name)
VALUES (nextval('clients_seq'), 'Иван Петров'),
       (nextval('clients_seq'), 'Анна Смирнова'),
       (nextval('clients_seq'), 'Сергей Кузнецов'),
       (nextval('clients_seq'), 'Мария Волкова'),
       (nextval('clients_seq'), 'Алексей Соколов');

-- ==============================
-- 3. Адреса клиентов
-- ==============================
INSERT INTO addresses (id, street, client_id)
VALUES (nextval('addresses_seq'), 'ул. Ленина, д. 1', 1),
       (nextval('addresses_seq'), 'пр. Мира, д. 25', 2),
       (nextval('addresses_seq'), 'ул. Советская, д. 12', 3),
       (nextval('addresses_seq'), 'ул. Гагарина, д. 5', 4),
       (nextval('addresses_seq'), 'ул. Пушкина, д. 7', 5);

-- ==============================
-- 4. Телефоны клиентов
-- ==============================
INSERT INTO phones (id, number, client_id)
VALUES
    -- Иван Петров
    (nextval('phones_seq'), '+7 (900) 111-11-11', 1),
    (nextval('phones_seq'), '+7 (900) 111-11-12', 1),

    -- Анна Смирнова
    (nextval('phones_seq'), '+7 (901) 222-22-21', 2),
    (nextval('phones_seq'), '+7 (901) 222-22-22', 2),

    -- Сергей Кузнецов
    (nextval('phones_seq'), '+7 (902) 333-33-31', 3),
    (nextval('phones_seq'), '+7 (902) 333-33-32', 3),

    -- Мария Волкова
    (nextval('phones_seq'), '+7 (903) 444-44-41', 4),
    (nextval('phones_seq'), '+7 (903) 444-44-42', 4),

    -- Алексей Соколов
    (nextval('phones_seq'), '+7 (904) 555-55-51', 5),
    (nextval('phones_seq'), '+7 (904) 555-55-52', 5);
