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

create table images
(
    id           SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    url         VARCHAR(255) NOT NULL,
	gallery_id  int NOT NULL
);

INSERT INTO images (name, url, gallery_id)
VALUES
		('birthday', 'path/image1', 1),
		('birthday', 'path/image2', 1),
        ('vacation', 'hawaii island was amazing', 2),
	    ('vacation', 'hawaii island was amazing', 2),
	    ('vacation', 'hawaii island was amazing', 2),
        ('nba finals', 'nba finals 2021-2022', 3)