package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;
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

    private void createCard() {
    }

    private void moveCardToNextColumn() {
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("\tColumn [%s]\nType [%s]\nCards amount [%s]", c.name(), c.kind(), c.cardsAmount());
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


    private void showCard() {
    }
}
