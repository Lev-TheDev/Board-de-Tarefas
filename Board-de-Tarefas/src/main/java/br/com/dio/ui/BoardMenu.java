package br.com.dio.ui;

import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.CardQueryService;
import br.com.dio.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Welcome to the %s board!\nChoose your option:\n", entity.getName());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Create a new card");
                System.out.println("2 - Move a card");
                System.out.println("3 - Block a card");
                System.out.println("4 - Unblock a card");
                System.out.println("5 - Cancel a card");
                System.out.println("6 - Show board");
                System.out.println("7 - Show columns with cards");
                System.out.println("8 - Show cards");
                System.out.println("9 - Head back to a previous menu");
                System.out.println("10 - Exit");

                try {
                    option = scanner.nextInt();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid option. Please enter a number.");
                    continue;
                }
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Heading back to main menu...");
                    case 10 -> {
                        System.out.println("Exiting the application. Goodbye!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.exit(0);
        }
    }

    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Input the card title:");
        card.setTitle(scanner.next());
        System.out.println("Input the card description:");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                new CardService(connection).create(card);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Input the card id you want to move to the next column:");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new br.com.dio.dto.BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind())).toList();
        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
            System.out.printf("Card with id [%s] successfully moved to the next column!", cardId);
        } catch (RuntimeException e) {
            System.out.println("An error occurred while moving the card: " + e.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Input the card id you want to block:");
        var cardId = scanner.nextLong();
        System.out.println("Input the reason of the block you want to apply:");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new br.com.dio.dto.BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind())).toList();
        try(var connection = getConnection()){
            connection.setAutoCommit(false);
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
            connection.commit();
            System.out.printf("Card with id [%s] successfully blocked!\n", cardId);
        } catch (RuntimeException e){
            System.out.println("An error occurred while blocking the card: " + e.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Input the card id you want to unblock:");
        var cardId = scanner.nextLong();
        System.out.println("Input the reason of the unblock you want to apply:");
        var reason = scanner.next();
        try(var connection = getConnection()){
            connection.setAutoCommit(false);
            new CardService(connection).unblock(cardId, reason);
            connection.commit();
            System.out.printf("Card with id [%s] successfully unblocked!\n", cardId);
        } catch (RuntimeException e){
            System.out.println("An error occurred while unblocking the card: " + e.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Input the card id you want to cancel:");
        var cardId = scanner.nextLong();
        var canceledColumn = entity.getCanceledColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new br.com.dio.dto.BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind())).toList();
        try (var connection = getConnection()) {
            new CardService(connection).cancel(cardId, canceledColumn.getId(), boardColumnsInfo);
            System.out.printf("Card with id [%s] successfully canceled!", cardId);
        } catch (RuntimeException e) {
            System.out.println("An error occurred while canceling the card: " + e.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("\tColumn [%s]\nType [%s]\nCards amount [%s]\n", c.name(), c.kind(), c.cardsAmount());
                });
            });

        } catch (Exception e){
            System.out.println("An error occurred while fetching the board details: " + e.getMessage());
        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns()
                .stream().map(BoardColumnEntity::getId)
                .toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)) {
            System.out.printf("Choose the column from board [%s] you want to see:\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try (var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Column [%s]\nType [%s]\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("\tCard [%s]\nDescription [%s]\n", ca.getTitle(), ca.getDescription()));
            });
        }
    }


    private void showCard() throws SQLException {
        System.out.println("Input the card id you want to see:");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId).ifPresentOrElse(
                    c -> {
                System.out.printf("Card: %s - %s.\n", c.id(), c.title());
                        System.out.printf("Description: %s.\n", c.description());
                        System.out.println(c.blocked() ? "This card is currently blocked. Reason: " + c.blockedReason() : "This card is currently unblocked.");
                        System.out.printf("Been blocked %s times.\n", c.blocksAmount());
                        System.out.printf("Current column: %s - %s\n", c.columnId(), c.columnName());
            },
                () -> System.out.println("Card not found with id: " + selectedCardId));
        }
    }
}
