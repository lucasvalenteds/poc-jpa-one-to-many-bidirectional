ALTER TABLE DOCUMENT
    ADD COLUMN PERSON_ID BIGINT;

ALTER TABLE DOCUMENT
    ADD FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID);