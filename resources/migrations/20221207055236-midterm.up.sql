CREATE TABLE midterm (
  id SERIAL PRIMARY KEY,
  num INTEGER,       -- use join?
  login VARCHAR(20), -- use join?
  answer_id INTEGER,
  grading VARCHAR(10),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
