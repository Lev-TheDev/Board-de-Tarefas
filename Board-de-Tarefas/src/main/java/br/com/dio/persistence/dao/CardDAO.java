package br.com.dio.persistence.dao;

import br.com.dio.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql = """
                SELECT c.id,
                       c.title,
                       c.description,
                       b.blocked_at,
                       b.block_reason,
                       c.board_column_id,
                       bc.name,
                       COUNT(SELECT sub_b.id
                            FROM BLOCKS sub_b
                            WHERE sub_b.card_id = c.id) blocks_amount
                FROM cards c
                LEFT JOIN BLOCKS b
                    ON b.card_id = c.id
                    AND b.unblocked_at IS NULL
                INNER JOIN BOARD_COLUMNS bc
                    ON bc.id = c.board_column_id
                WHERE id = ?
                """;
        try (var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()){
                return Optional.of(new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        //resultSet.getTimestamp("b.blocked_at") != null,
                        resultSet.getString("b.block_reason").isEmpty(),
                        toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
                        //resultSet.getObject("blocked_at", OffsetDateTime.class),
                        resultSet.getString("b.block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name")
                ));
            }
        }
        return Optional.empty();
    }
}
