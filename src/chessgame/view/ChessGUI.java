package chessgame.view;

import chessgame.model.Board;
import chessgame.model.Color;
import chessgame.model.Position;
import chessgame.model.pieces.IPiece;
import chessgame.util.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class ChessGUI extends JFrame {
    private Board board;
    private chessgame.model.Color turn;
    private JButton[][] buttons = new JButton[8][8];
    private JLabel statusLabel = new JLabel("Turno: BLANCAS");
    private chessgame.model.Position selected = null;
    private List<Board.Move> legalMovesTurn;

    public ChessGUI(Board board, chessgame.model.Color turn) {
        this.board = board;
        this.turn = turn;
        setTitle("Ajedrez");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 800);
        setLayout(new BorderLayout());

        // --- Panel de tablero alineado con letras y números ---
        JPanel boardGrid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Letras columna arriba
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 8;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel colLettersTop = new JPanel(new GridLayout(1, 8, 0, 0));
        for (int c = 0; c < 8; c++) {
            JLabel label = new JLabel(String.valueOf((char)('A'+c)), SwingConstants.CENTER);
            label.setFont(new Font("Monospaced", Font.BOLD, 18));
            colLettersTop.add(label);
        }
        boardGrid.add(colLettersTop, gbc);

        // Letras columna abajo
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 8;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel colLettersBottom = new JPanel(new GridLayout(1, 8, 0, 0));
        for (int c = 0; c < 8; c++) {
            JLabel label = new JLabel(String.valueOf((char)('A'+c)), SwingConstants.CENTER);
            label.setFont(new Font("Monospaced", Font.BOLD, 18));
            colLettersBottom.add(label);
        }
        boardGrid.add(colLettersBottom, gbc);

        // Números de filas a la izquierda
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 8;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel rowNumbersLeft = new JPanel(new GridLayout(8,1,0,0));
        for (int i = 0; i < 8; i++) {
            JLabel l = new JLabel(String.valueOf(8-i), SwingConstants.CENTER);
            l.setFont(new Font("Monospaced", Font.BOLD, 18));
            rowNumbersLeft.add(l);
        }
        boardGrid.add(rowNumbersLeft, gbc);

        // Números de filas a la derecha
        gbc.gridx = 9;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 8;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel rowNumbersRight = new JPanel(new GridLayout(8,1,0,0));
        for (int i = 0; i < 8; i++) {
            JLabel r = new JLabel(String.valueOf(8-i), SwingConstants.CENTER);
            r.setFont(new Font("Monospaced", Font.BOLD, 18));
            rowNumbersRight.add(r);
        }
        boardGrid.add(rowNumbersRight, gbc);

        // Panel central del tablero (botones con símbolos)
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 8;
        gbc.gridheight = 8;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel boardPanel = new JPanel(new GridLayout(8,8,0,0));
        for (int i=0; i<8; i++) for (int j=0; j<8; j++) {
            JButton btn = new JButton();
            btn.setFont(new Font("Monospaced", Font.BOLD, 32));
            btn.setOpaque(true);
            btn.setBackground((i+j)%2==0 ? new java.awt.Color(240,217,181) : new java.awt.Color(181,136,99));
            int row = i, col = j;
            btn.addActionListener(e -> onSquareClicked(row, col));
            buttons[i][j] = btn;
            boardPanel.add(btn);
        }
        boardGrid.add(boardPanel, gbc);

        // --- Panel lateral con opciones ---
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Opciones"));

        JButton saveBtn = new JButton("1 - Guardar partida");
        JButton loadBtn = new JButton("2 - Cargar partida");
        JButton newBtn = new JButton("3 - Iniciar una nueva partida");
        JButton exitBtn = new JButton("4 - Salir");

        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        newBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        saveBtn.addActionListener(e -> saveGame());
        loadBtn.addActionListener(e -> loadGame());
        newBtn.addActionListener(e -> newGame());
        exitBtn.addActionListener(e -> System.exit(0));

        optionsPanel.add(Box.createVerticalStrut(30));
        optionsPanel.add(saveBtn);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(loadBtn);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(newBtn);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(exitBtn);
        optionsPanel.add(Box.createVerticalGlue());

        // --- Panel para el status y el tablero ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(boardGrid, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(optionsPanel, BorderLayout.EAST);

        refreshLegalMovesTurn();
        refreshBoard();
        setVisible(true);
    }

    private void refreshLegalMovesTurn() {
        legalMovesTurn = board.getLegalMovesForTurn(turn);
    }

    private void onSquareClicked(int row, int col) {
        if (selected == null) {
            IPiece piece = board.getPieceAt(row, col);
            if (piece == null || piece.getColor() != turn) {
                showMessage("Selecciona una pieza de tu color.");
                return;
            }
            boolean hasMove = false;
            for (Board.Move m : legalMovesTurn) {
                if (m.from.equals(new Position(row, col))) {
                    hasMove = true; break;
                }
            }
            if (!hasMove) {
                showMessage("Movimiento no permitido.");
                return;
            }
            selected = new chessgame.model.Position(row, col);
            highlightMoves(selected);
        } else {
            if (selected.getRow() == row && selected.getCol() == col) {
                selected = null;
                refreshBoard();
                return;
            }
            Position dest = new Position(row, col);
            boolean isLegal = false;
            for (Board.Move m : legalMovesTurn) {
                if (m.from.equals(selected) && m.to.equals(dest)) {
                    isLegal = true; break;
                }
            }
            if (!isLegal) {
                showMessage("Movimiento no permitido.");
                selected = null;
                refreshBoard();
                return;
            }
            try {
                board.movePiece(selected, dest, turn);
                turn = (turn==chessgame.model.Color.WHITE) ? chessgame.model.Color.BLACK : chessgame.model.Color.WHITE;
                selected = null;
                refreshLegalMovesTurn();
                refreshBoard();
                checkGameStatus();
            } catch (Exception ex) {
                showMessage(ex.getMessage());
                selected = null;
                refreshBoard();
            }
        }
    }

    private void refreshBoard() {
        for (int i=0; i<8; i++) for (int j=0; j<8; j++) {
            IPiece p = board.getPieceAt(i, j);
            String txt = (p == null) ? "" : String.valueOf(p.getSymbol());
            buttons[i][j].setText(txt);
            buttons[i][j].setBorder(UIManager.getBorder("Button.border"));
            buttons[i][j].setBackground((i+j)%2==0 ? new java.awt.Color(240,217,181) : new java.awt.Color(181,136,99));
        }
        statusLabel.setText("Turno: " + (turn==chessgame.model.Color.WHITE ? "BLANCAS" : "NEGRAS"));
    }

    private void highlightMoves(chessgame.model.Position pos) {
        refreshBoard();
        for (Board.Move m : legalMovesTurn) {
            if (m.from.equals(pos)) {
                buttons[m.to.getRow()][m.to.getCol()].setBackground(java.awt.Color.YELLOW);
            }
        }
        buttons[pos.getRow()][pos.getCol()].setBorder(BorderFactory.createLineBorder(java.awt.Color.BLUE, 3));
    }

    private void checkGameStatus() {
        if (board.isInCheck(turn)) {
            refreshLegalMovesTurn();
            if (legalMovesTurn.isEmpty()) {
                showMessage("¡Jaque mate! " + (turn==chessgame.model.Color.WHITE ? "Ganan NEGRAS" : "Ganan BLANCAS"));
                newGame();
            } else {
                showMessage("¡" + (turn==chessgame.model.Color.WHITE ? "Blancas" : "Negras") + " están en jaque!");
            }
        } else {
            refreshLegalMovesTurn();
        }
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    private void newGame() {
        board.init();
        turn = chessgame.model.Color.WHITE;
        selected = null;
        refreshLegalMovesTurn();
        refreshBoard();
        statusLabel.setText("Turno: BLANCAS");
    }

    private void saveGame() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                FileManager<Object[]> fm = new FileManager<>();
                fm.saveToFile(new Object[]{board, turn}, file.getAbsolutePath());
                showMessage("Partida guardada.");
            } catch (Exception ex) {
                showMessage("Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void loadGame() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                FileManager<Object[]> fm = new FileManager<>();
                Object[] data = fm.loadFromFile(file.getAbsolutePath());
                board = (Board) data[0];
                turn = (chessgame.model.Color) data[1];
                selected = null;
                refreshLegalMovesTurn();
                refreshBoard();
                showMessage("Partida cargada.");
            } catch (Exception ex) {
                showMessage("Error al cargar: " + ex.getMessage());
            }
        }
    }
}