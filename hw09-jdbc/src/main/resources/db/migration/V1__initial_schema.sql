create table manager
(
    no      bigserial primary key,
    label   varchar(255),
    param1  varchar(255)
);

create table client
(
    id      bigserial primary key,
    name    varchar(50)
);
