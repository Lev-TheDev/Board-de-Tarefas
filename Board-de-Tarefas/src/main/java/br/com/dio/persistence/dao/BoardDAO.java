package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardDAO {

    private final Connection connection;

    public BoardEntity insert(BoardEntity boardEntity) throws SQLException {
        // Lógica para inserir o boardEntity no banco de dados
        var sql = "INSERT INTO BOARDS (name) VALUES (?)";
        //try(var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("A conexão está fechada.");
        }
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, boardEntity.getName());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                boardEntity.setId(impl.getLastInsertID());
            }
        }
        return boardEntity;
            // var generatedKeys = statement.getGeneratedKeys();
            //if (generatedKeys.next()) {
            //    boardEntity.setId(generatedKeys.getLong(1));
            //}
    }

    public void delete(Long id) throws SQLException {
        // Lógica para deletar o boardEntity do banco de dados pelo id
        var sql = "DELETE FROM BOARDS WHERE id = ?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public Optional<BoardEntity> findById(Long id) throws SQLException {
        // Lógica para buscar o boardEntity no banco de dados pelo id
        var sql = "SELECT id, name FROM BOARDS WHERE id = ?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var boardEntity = new BoardEntity();
                boardEntity.setId(resultSet.getLong("id"));
                boardEntity.setName(resultSet.getString("name"));
                return Optional.of(boardEntity);
            } else {
                return Optional.empty();
            }
            // pode ser que haja um erro no código acima
        }
    }

    public boolean exists(Long id) throws SQLException {
        // Lógica para verificar se o boardEntity existe no banco de dados pelo id
        var sql = "SELECT 1 FROM BOARDS WHERE id = ?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try(var resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

}
