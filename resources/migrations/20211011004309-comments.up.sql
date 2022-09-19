CREATE TABLE comments (
  id SERIAL PRIMARY KEY,
  comment TEXT,
  from_login VARCHAR(20),
  to_login   VARCHAR(20),
  p_num INT,
  a_id  INT,
  c_id  INT,
  create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
