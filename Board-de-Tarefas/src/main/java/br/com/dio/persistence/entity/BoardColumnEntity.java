package br.com.dio.persistence.entity;

import lombok.Data;
import lombok.ToString;

@Data
public class BoardColumnEntity {

    private Long id;
    private String name;
    private int order;
    private BoardColumnKindEnum kind;
    private BoardEntity board = new BoardEntity();
}
