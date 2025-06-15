package chessgame.controller;

import chessgame.model.*;
import chessgame.util.FileManager;

import java.util.Scanner;

public class GameController {
    private Board board;
    private Color currentTurn;
    private boolean running = true;
    private final Scanner scanner = new Scanner(System.in);

    public GameController() {
        board = new Board();
        currentTurn = Color.WHITE;
    }

    public void start() {
        while (running) {
            showMenu();
        }
    }

    private void showMenu() {
        System.out.println("Seleccione opción:");
        System.out.println("1-Iniciar partida nueva");
        System.out.println("2-Cargar Partida");
        System.out.println("3-Salir");
        String op = scanner.nextLine();
        switch (op) {
            case "1" -> newGame();
            case "2" -> loadGame();
            case "3" -> running = false;
            default -> System.out.println("Opción inválida.");
        }
    }

    private void showGameMenu() {
        System.out.println("Opciones:");
        System.out.println("1-Guardar partida");
        System.out.println("2-Cargar partida");
        System.out.println("3-Nueva partida");
        System.out.println("4-Salir");
        String op = scanner.nextLine();
        switch (op) {
            case "1" -> saveGame();
            case "2" -> loadGame();
            case "3" -> newGame();
            case "4" -> running = false;
            default -> System.out.println("Opción inválida.");
        }
    }

    private void newGame() {
        board = new Board();
        currentTurn = Color.WHITE;
        playGame();
    }

    private void loadGame() {
        System.out.print("Ingrese el nombre del archivo: ");
        String filename = scanner.nextLine();
        try {
            FileManager<Board> fm = new FileManager<>();
            board = fm.loadFromFile(filename);
            currentTurn = Color.WHITE; // O puedes guardar el turno en el archivo
            System.out.println("Partida cargada.");
            playGame();
        } catch (Exception e) {
            System.out.println("Error al cargar: " + e.getMessage());
        }
    }

    private void saveGame() {
        System.out.print("Ingrese el nombre del archivo: ");
        String filename = scanner.nextLine();
        try {
            FileManager<Board> fm = new FileManager<>();
            fm.saveToFile(board, filename);
            System.out.println("Partida guardada.");
        } catch (Exception e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    private void playGame() {
        boolean gameOngoing = true;
        while (gameOngoing && running) {
            board.printBoard();
            if (board.isInCheck(currentTurn)) {
                if (board.isCheckmate(currentTurn)) {
                    System.out.println("¡Jaque mate! Ganan las " + (currentTurn == Color.WHITE ? "negras" : "blancas"));
                    gameOngoing = false;
                    break;
                } else {
                    System.out.println((currentTurn == Color.WHITE ? "Blancas" : "Negras") + " están en jaque.");
                }
            }
            System.out.println("Turno de " + (currentTurn == Color.WHITE ? "blancas" : "negras"));
            System.out.print("Ingrese movimiento (ejemplo: e2e4) o 'menu': ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("menu")) {
                showGameMenu();
                if (!running) break;
                continue;
            }
            if (input.length() != 4) {
                System.out.println("Formato inválido.");
                continue;
            }
            try {
                Position from = Position.fromChessCoords(input.substring(0,2));
                Position to = Position.fromChessCoords(input.substring(2,4));
                board.movePiece(from, to, currentTurn);
                currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}