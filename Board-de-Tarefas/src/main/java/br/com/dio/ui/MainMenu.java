package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Welcome to the Task Board!");
        System.out.println("Please select an option:");
        var option = -1;
        while (true){
            System.out.println("1 - Create a new board");
            System.out.println("2 - Select an existing board");
            System.out.println("3 - Delete a board");
            System.out.println("4 - Exit");
            try {
                option = scanner.nextInt();
            } catch (NumberFormatException e){
                System.out.println("Invalid option. Please enter a number.");
                continue;
            }
            switch (option){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> {
                    System.out.println("Exiting the application. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createBoard() throws SQLException {
        System.out.println("Insert the name of the board:");
        var boardEntity = new br.com.dio.persistence.entity.BoardEntity();
        boardEntity.setName(scanner.next());
        System.out.println("Does the board has columns beside the 3 default ones? If YES, insert the number; if NO, insert 0 (zero):");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Insert the name of the initial column:");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, BoardColumnKindEnum.INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Insert the name of the pending task column:");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, BoardColumnKindEnum.PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Insert the name of the final column:");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, BoardColumnKindEnum.FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Insert the name of the canceling board column:");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, BoardColumnKindEnum.CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        boardEntity.setBoardColumns(columns);
        try (var connection = getConnection()){
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Failed to establish a database connection.");
            }
            connection.setAutoCommit(true);
            var service = new BoardService(connection);
            service.insert(boardEntity);
            //connection.commit();
            System.out.printf("Board %s created successfully with id %s.\n", boardEntity.getName(), boardEntity.getId());
        } catch (SQLException e) {
            System.err.println("Error while creating the board: " + e.getMessage());
            throw e;
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Insert the id of the board to select:");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {
            connection.setAutoCommit(true);
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Board not found. Please check the id (%s) and try again.\n", id)
            );
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Insert the id of the board to delete:");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            connection.setAutoCommit(true);
            var service = new BoardService(connection);
            if (service.delete(id)){
                System.out.printf("Board %s deleted successfully.\n", id);
            } else {
                System.out.printf("Board not found. Please check the id (%s) and try again.\n", id);
            }
            //connection.commit();
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind,final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
