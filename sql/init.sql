create database if not exists gallery;
create database if not exists image;

create table galleries
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  VARCHAR(255) NOT NULL
);

INSERT INTO galleries (title, description)
VALUES ('birthday', 'birthday at work'),
       ('vacation', 'hawaii island was amazing'),
       ('nba finals', 'nba finals 2021-2022');
