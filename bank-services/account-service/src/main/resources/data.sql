DROP TABLE IF EXISTS account;

CREATE TABLE account
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    type        ENUM ('CURRENT', 'SAVING') default 'CURRENT',
    balance     DECIMAL(10, 2)             DEFAULT 0.0
);
