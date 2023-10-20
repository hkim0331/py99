create table actions (
  id SERIAL PRIMARY KEY,
  login varchar(20),
  action varchar(100),
  num int,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
