CREATE TABLE IF NOT EXISTS REPORT (
  id int AUTO_INCREMENT PRIMARY KEY,
  pokestop VARCHAR,
  task VARCHAR,
  reward VARCHAR,
  latitude DOUBLE,
  longitude DOUBLE,
  reported_at DATE
);
