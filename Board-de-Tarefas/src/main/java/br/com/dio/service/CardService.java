package br.com.dio.service;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardFinishedException;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity create(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            connection.setAutoCommit(false);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("Card with id %d not found".formatted(cardId)));
            if (dto.blocked()){
                throw new CardBlockedException("Card with id %d is blocked and cannot be moved".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Informed Card belongs to another Board."));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("Card with id %d is already in the final column and cannot be moved".formatted(cardId));
            }
            var nextColumn = boardColumnsInfo.stream().filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Card is cancelled and cannot be moved."));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException e){
            connection.rollback();
            throw e;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            connection.setAutoCommit(false);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("Card with id %d not found".formatted(cardId)));
            if (dto.blocked()){
                throw new CardBlockedException("Card with id %d is blocked and cannot be moved".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Informed Card belongs to another Board."));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("Card with id %d is already in the final column and cannot be moved".formatted(cardId));
            }
            boardColumnsInfo.stream().filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Card is cancelled and cannot be moved."));
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException e){
            connection.rollback();
            throw e;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            var blockDAO = new BlockDAO(connection);
            connection.setAutoCommit(false);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %d not found".formatted(id)));
            if (dto.blocked()){
                throw new CardBlockedException("Card with id %d is already blocked".formatted(id));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Informed Card belongs to another Board."));
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                throw new IllegalStateException("The Card belongs to a %s type column and cannot be blocked"
                        .formatted(currentColumn.kind().name()));
            }
            blockDAO.block(reason, id);
            connection.commit();
        } catch (SQLException e){
            connection.rollback();
            throw e;
        }
    }

}
