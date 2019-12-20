# --- !Ups

create table users
(
    user_id   varchar(36) default uuid() primary key,
    user_code varchar(250) not null,
    password  varchar(250) not null
);

insert into users values (uuid(), 'stest', '$2a$10$niF.amAexQMHaevqlkganeSjvMHfTq/OdISyj8/5BQy1FHvlbi3Ne');

# --- !Downs

drop table users;
