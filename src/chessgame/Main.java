package chessgame;

import chessgame.model.Board;
import chessgame.model.Color;
import chessgame.view.ChessGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessGUI(new Board(), chessgame.model.Color.WHITE));
    }
}