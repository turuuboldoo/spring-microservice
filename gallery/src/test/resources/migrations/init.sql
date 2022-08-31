drop table if exists galleries;

create table galleries
(
    id          serial primary key,
    title       varchar,
    description varchar
);