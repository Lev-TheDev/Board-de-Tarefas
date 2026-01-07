--liquibase formatted sql
--changeset lev:202607011216
--comment: boards table create

CREATE TABLE BOARDS(
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

--rollback DROP TABLE BOARDS