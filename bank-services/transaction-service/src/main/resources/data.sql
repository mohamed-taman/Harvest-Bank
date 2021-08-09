DROP TABLE IF EXISTS account_transaction;

CREATE TABLE account_transaction
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT,
    type       ENUM ('CREDIT', 'DEBIT') default 'CREDIT',
    amount     DECIMAL(10, 2)           DEFAULT 0.0
);
