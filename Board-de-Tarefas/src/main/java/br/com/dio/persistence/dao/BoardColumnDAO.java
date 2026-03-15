package br.com.dio.persistence.dao;

import br.com.dio.dto.BoardColumnDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.CardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.findByName;

@RequiredArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException{
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?)";
        try(var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i++, entity.getBoard().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
            return entity;
        }
    }

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        var sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            List<BoardColumnEntity> entities = new ArrayList<>();
            while (resultSet.next()) {
                var boardColumnEntity = new BoardColumnEntity();
                boardColumnEntity.setId(resultSet.getLong("id"));
                boardColumnEntity.setName(resultSet.getString("name"));
                boardColumnEntity.setOrder(resultSet.getInt("order"));
                boardColumnEntity.setKind(findByName(resultSet.getString("kind")));
                entities.add(boardColumnEntity);
            }
            return entities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql = """
        SELECT bc.id,
               bc.name,
               bc.kind,
               COUNT(SELECT c.id
                FROM CARDS c
                WHERE c.board_column_id = bc.id) cards_amount
         FROM BOARDS_COLUMNS
         WHERE board_id = ?
         ORDER BY `order`
        """;
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            List<BoardColumnEntity> entities = new ArrayList<>();
            while (resultSet.next()) {
                var dto = new BoardColumnDTO(resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        findByName(resultSet.getString("bc.kind")),
                        resultSet.getInt("cards_amount")
                );
                dtos.add(dto);
            }
            return dtos;
        }
    }

    public Optional<BoardColumnEntity> findById(final Long boardId) throws SQLException {
        var sql =
                """
                SELECT bc.name,
                bc.kind
                c.id,
                c.title,
                c.description,
                FROM BOARDS_COLUMNS bc
                INNER JOIN CARDS c ON c.board_column_id = bc.id
                WHERE bc.id = ?;
                """;
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){
                var boardColumnEntity = new BoardColumnEntity();
                boardColumnEntity.setName(resultSet.getString("bc.name"));
                boardColumnEntity.setKind(findByName(resultSet.getString("bc.kind")));
                do {
                    var cardEntity = new CardEntity();
                    cardEntity.setId(resultSet.getLong("c.id"));
                    cardEntity.setTitle(resultSet.getString("c.title"));
                    cardEntity.setDescription(resultSet.getString("c.description"));
                    boardColumnEntity.getCards().add(cardEntity);
                } while (resultSet.next());
            }
            return Optional.empty();
        }
    }

}
