package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.util.Scanner;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final BoardEntity entity;

    public void execute() {
        System.out.printf("Welcome to the %s board!\nChoose your option:\n", entity.getName());
        var option = -1;
        while (option != 9){
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
            } catch (NumberFormatException e){
                System.out.println("Invalid option. Please enter a number.");
                continue;
            }
            switch (option){
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

    private void showBoard() {
    }

    private void showColumn() {
    }

    private void showCard() {
    }
}
