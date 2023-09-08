CREATE TABLE IF NOT EXISTS dead_letter (
  id UUID PRIMARY KEY,
  service TEXT NOT NULL,
  topic TEXT NOT NULL,
  payload TEXT NOT NULL,
  exception_info TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  replay_count INT NOT NULL,
  status TEXT NOT NULL
);