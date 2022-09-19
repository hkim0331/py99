create table problems (
  id serial primary key,
  num integer,
  is_avail integer default 1,
  problem text not null,
  test text default '',
  create_at timestamp default current_timestamp,
  update_at timestamp default current_timestamp);
