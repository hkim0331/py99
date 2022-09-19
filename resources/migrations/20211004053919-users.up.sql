create table users (
  id serial primary key,
  sid varchar(10) not null unique,
  login varchar(20) not null unique,
  name varchar(30) not null,
  password varchar(300),
  is_admin BOOLEAN default false,
  create_at timestamp default current_timestamp,
  update_at timestamp default current_timestamp);
