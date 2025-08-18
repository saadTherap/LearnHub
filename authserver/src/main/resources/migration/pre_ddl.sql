-- Sequence for users.id
CREATE SEQUENCE users_seq
    START WITH 1
    INCREMENT BY 5
    NOCACHE
    NOCYCLE;

-- Table for User entity
CREATE TABLE users (
    id        NUMBER(19)         NOT NULL PRIMARY KEY,
    email     VARCHAR(255) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(50)  NOT NULL,
    enabled   BOOLEAN      NOT NULL
);