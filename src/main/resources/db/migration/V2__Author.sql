create table author
(
    id     serial primary key,
    name   varchar(100)  not null,
    created_at timestamp not null default current_timestamp
);