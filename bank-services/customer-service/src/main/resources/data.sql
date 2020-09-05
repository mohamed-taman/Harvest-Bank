DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  first_name VARCHAR(250) NOT NULL,
  last_name VARCHAR(250) NOT NULL,
  balance DECIMAL(10, 2) DEFAULT 0.0
);

INSERT INTO customer (first_name, last_name, balance) VALUES
  ('Mohamed', 'Taman', 200.20),
  ('Issa', 'Ahmed', 100.30),
  ('Matin', 'Abbasi', 10.0),
  ('Milica', 'Jovicic', 0.0),
  ('Angèl', 'Wijnhard', 0.0),
  ('Fatih', 'Akbas',0.0),
  ('Ibrahim', 'Moustafa', 0.0);