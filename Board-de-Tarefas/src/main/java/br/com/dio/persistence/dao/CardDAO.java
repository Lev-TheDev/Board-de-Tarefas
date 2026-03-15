package br.com.dio.persistence.dao;

import br.com.dio.dto.CardDetails;
import lombok.AllArgsConstructor;

import java.sql.Connection;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public CardDetails findById(final Long id){

        return null;
    }
}
