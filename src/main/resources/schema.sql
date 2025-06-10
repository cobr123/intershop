CREATE TABLE account(
    id      bigserial,
    name    varchar(255)   NOT NULL,
    balance numeric(15, 2) NOT NULL DEFAULT 10000.0,
    CONSTRAINT account_balance_non_negative CHECK (balance >= 0),
    PRIMARY KEY(id)
);