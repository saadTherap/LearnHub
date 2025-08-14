-- Pre-DML: Seed bcrypt-encoded initial users for Oracle
-- Password: <lowercase-role>123

-- Admin
INSERT INTO users (id, email, password, role, enabled, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'admin@example.com',
        '$2a$10$X7CKlYnHLeF8goVxM0Lg8OBJ8bzUdU3GTR/5BFP7jAJf3pgQ7eGaK',
        'ADMIN', 1, SYSDATE, SYSDATE);

-- Students
INSERT INTO users (id, email, password, role, enabled, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'student1@example.com',
        '$2a$10$9sxeX4IkQIkBphmAjF3QmOl6QyRH50Ajvm6mZxxoylDhrkwWCG1si',
        'STUDENT', 1, SYSDATE, SYSDATE);

INSERT INTO users (id, email, password, role, enabled, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'student2@example.com',
        '$2a$10$9sxeX4IkQIkBphmAjF3QmOl6QyRH50Ajvm6mZxxoylDhrkwWCG1si',
        'STUDENT', 1, SYSDATE, SYSDATE);

-- Instructors
INSERT INTO users (id, email, password, role, enabled, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'instructor1@example.com',
        '$2a$10$uQ/xQ2lxrOxSpU1RZIZcqOM9nFZ.VuAVgJ4qHgQ4rf7A/YOZ6wLZK',
        'INSTRUCTOR', 1, SYSDATE, SYSDATE);

INSERT INTO users (id, email, password, role, enabled, created_at, updated_at)
VALUES (users_seq.NEXTVAL, 'instructor2@example.com',
        '$2a$10$uQ/xQ2lxrOxSpU1RZIZcqOM9nFZ.VuAVgJ4qHgQ4rf7A/YOZ6wLZK',
        'INSTRUCTOR', 1, SYSDATE, SYSDATE);
