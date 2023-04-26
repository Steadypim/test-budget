alter table budget
    add column author_id int references author(id);
