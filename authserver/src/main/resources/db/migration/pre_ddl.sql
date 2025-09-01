-- DDL Script for Authentication Server Database (Oracle)
-- Generated based on JPA entities: User, VerificationToken, and Persistence
-- Cleaning up the Database
-- DROP TABLE verification_tokens CASCADE CONSTRAINTS;
-- DROP TABLE users CASCADE CONSTRAINTS;
--
-- -- Drop sequences
-- DROP SEQUENCE users_seq;
-- DROP SEQUENCE verification_tokens_seq;
-- DROP SEQUENCE token_seq;
--
-- -- Optional: Purge recyclebin to completely remove objects (Oracle 10g+)
-- PURGE RECYCLEBIN;

-- Create sequence for users table
CREATE SEQUENCE users_seq
    START WITH 1
    INCREMENT BY 5
    CACHE 5;

-- Create sequence for verification_tokens table
CREATE SEQUENCE token_seq
    START WITH 1
    INCREMENT BY 1
    CACHE 20;

-- Create users table
CREATE TABLE users (
    id NUMBER(19) PRIMARY KEY,
    email VARCHAR2(255) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    role VARCHAR2(50) NOT NULL,
    enabled NUMBER(1) DEFAULT 0 NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    is_deleted NUMBER(1) DEFAULT 0,
    version NUMBER(10)
);

-- Add check constraints for boolean fields (Oracle doesn't have native BOOLEAN)
ALTER TABLE users ADD CONSTRAINT chk_users_enabled CHECK (enabled IN (0, 1));
ALTER TABLE users ADD CONSTRAINT chk_users_is_deleted CHECK (is_deleted IN (0, 1));

-- Add indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_enabled ON users(enabled);
CREATE INDEX idx_users_is_deleted ON users(is_deleted);

-- Create verification_tokens table
CREATE TABLE verification_tokens (
     id NUMBER(19) PRIMARY KEY,
     token VARCHAR2(255) NOT NULL UNIQUE,
     user_id NUMBER(19) NOT NULL UNIQUE,
     expiry_date TIMESTAMP NOT NULL,
     CONSTRAINT fk_verification_tokens_user
         FOREIGN KEY (user_id) REFERENCES users(id)
             ON DELETE CASCADE
);

-- Add indexes for verification_tokens table
CREATE INDEX idx_verification_tokens_token ON verification_tokens(token);
CREATE INDEX idx_verification_tokens_user_id ON verification_tokens(user_id);
CREATE INDEX idx_verification_tokens_expiry_date ON verification_tokens(expiry_date);