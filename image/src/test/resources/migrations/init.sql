drop table if exists images;

create table if not exists images
(
    id          serial primary key,
    name        varchar,
    url         varchar,
    gallery_Id   long
);