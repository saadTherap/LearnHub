CREATE SEQUENCE avi_stored_file_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE avi_stored_file (
                                 id                NUMBER(19) PRIMARY KEY,
                                 original_file_name VARCHAR2(255),
                                 stored_file_name   VARCHAR2(255),
                                 content_type      VARCHAR2(255),
                                 upload_time       TIMESTAMP
);