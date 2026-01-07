--liquibase formatted sql
--changeset lev:202607011638
--comment: cards table create

CREATE TABLE CARDS(
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    TITLE VARCHAR(255) NOT NULL,
    DESCRIPTION VARCHAR(255) NOT NULL,
    `order` int NOT NULL,
    board_column_id BIGINT NOT NULL,
    CONSTRAINT boards_columns__cards_fk FOREIGN KEY (board_column_id) REFERENCES BOARDS_COLUMNS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--rollback DROP TABLE CARDS