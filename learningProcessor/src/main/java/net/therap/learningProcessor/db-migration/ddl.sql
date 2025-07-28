CREATE SEQUENCE avi_student_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE avi_student_content_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE avi_student_submission_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE avi_course_enrollment_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE avi_student (
     id              NUMBER(19) PRIMARY KEY,
     first_name      VARCHAR2(255),
     last_name       VARCHAR2(255),
     gender          VARCHAR2(10),
     date_of_birth   DATE,
     email           VARCHAR2(255) UNIQUE NOT NULL,
     phone           VARCHAR2(255),
     address         VARCHAR2(100),
     image_url       VARCHAR2(1024),
     is_deleted      NUMBER(1) DEFAULT 0 NOT NULL,
     created_at      TIMESTAMP NOT NULL,
     updated_at      TIMESTAMP NOT NULL,
     version         NUMBER(10) NOT NULL
);

CREATE TABLE avi_course_enrollment (
    id              NUMBER(19) PRIMARY KEY,
    student_id      NUMBER(19) NOT NULL,
    course_id       NUMBER(19) NOT NULL,
    is_deleted      NUMBER(1) DEFAULT 0 NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    version         NUMBER(10) NOT NULL,
    CONSTRAINT uk_course_enrollment UNIQUE (student_id, course_id),
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES  avi_student(id)
);

CREATE TABLE avi_student_content (
    id              NUMBER(19) PRIMARY KEY,
    student_id      NUMBER(19) NOT NULL,
    content_id      NUMBER(19) NOT NULL,
    status          VARCHAR2(30) NOT NULL,
    is_deleted      NUMBER(1) DEFAULT 0 NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    version         NUMBER(10) NOT NULL,
    CONSTRAINT uk_student_content UNIQUE (student_id, content_id),
    CONSTRAINT fk_student_content FOREIGN KEY (student_id) REFERENCES  avi_student(id)
);

CREATE TABLE avi_student_submission (
    id              NUMBER(19) PRIMARY KEY,
    student_id      NUMBER(19) NOT NULL,
    content_id      NUMBER(19) NOT NULL,
    submitted_at    TIMESTAMP NOT NULL,
    download_url    VARCHAR2(1024),
    is_deleted      NUMBER(1) DEFAULT 0 NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    version         NUMBER(10) NOT NULL,
    CONSTRAINT uk_student_submission UNIQUE (student_id, content_id),
    CONSTRAINT fk_student_submission FOREIGN KEY (student_id) REFERENCES  avi_student(id)
);