CREATE SEQUENCE PERSON_ID_SEQUENCE START WITH 1;

CREATE TABLE PERSON
(
    PERSON_ID   BIGINT,
    PERSON_NAME VARCHAR,

    PRIMARY KEY (PERSON_ID)
);