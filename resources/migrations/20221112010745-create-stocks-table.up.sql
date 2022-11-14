create table stocks (
  id serial primary key,
  login varchar(255) not null,
  a_id  int not null,
  timestamp TIMESTAMP default CURRENT_TIMESTAMP);
