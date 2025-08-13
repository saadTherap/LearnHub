INSERT INTO users (id, email, password, role, enabled)
VALUES (users_seq.NEXTVAL, 'admin@example.com', 'admin123', 'ADMIN', 1);

INSERT INTO users (id, email, password, role, enabled)
VALUES (users_seq.NEXTVAL, 'user1@example.com', 'user123', 'USER', 1);

INSERT INTO users (id, email, password, role, enabled)
VALUES (users_seq.NEXTVAL, 'disabled@example.com', 'disabled123', 'USER', 0);
