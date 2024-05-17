package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayBoard {
    private String Difficulty;
    private Board board;
    private HashMap<ArrayList<Integer>, String> revealedCells = new HashMap<>();

    // 0 = game in progress, 1 = game won, -1 = game lost
    private int gameStatus = 0;

    private JPanel grid = new JPanel();
    private JFrame frame = new JFrame();
    private int flags;

    private JLabel faceLabel;

    public DisplayBoard(String Difficulty) {
        this.Difficulty = Difficulty;
        switch (Difficulty) {
            case "Medium" -> this.board = new Board(16, 16, 40);
            case "Hard" -> this.board = new Board(16, 30, 99);
            default -> this.board = new Board(9, 9, 10);
        }
        this.flags = this.board.getNumberOfMines();
        initializeGame();
    }
    public DisplayBoard() {
        this("Easy");
    }


    public void displayGUI() {
            frame.pack();
            frame.setVisible(true);

            // Wait for the JFrame to be closed
            while (frame.isVisible()) {
                try {
                    Thread.sleep(1000); // Sleep for a short time to avoid excessive CPU usage
                } catch (InterruptedException e) {
                    System.out.println("Error occurred while sleeping: " + e.getMessage());
                }
            }
    }

    public void initializeGame(){
        JPanel gridPanel = getjPanel();

        // Create the header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel flagLabel = new JLabel("Flags: " + flags);
        JLabel mineLabel = new JLabel("Mines: " + board.getNumberOfMines());
        flagLabel.setPreferredSize(new Dimension(100, 50));
        this.faceLabel = new JLabel(getFaceIcon(gameStatus));
        addFaceLabelMouseListener();
        headerPanel.add(flagLabel, BorderLayout.WEST);
        headerPanel.add(mineLabel, BorderLayout.EAST);
        headerPanel.add(faceLabel, BorderLayout.CENTER);

        // Create the JFrame
        JFrame frame = new JFrame();
        frame.add(headerPanel, BorderLayout.NORTH); // Add the header panel to the north
        frame.add(gridPanel, BorderLayout.CENTER); // Add the grid panel to the center
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Perform any necessary cleanup or exit the program here
                System.exit(0); // Exit the program
            }
        });

        this.frame = frame;
    }

    private JPanel getjPanel() {
        int[][] boardMatrix = this.board.getBoardMatrix();
        JPanel gridPanel = this.grid;
        gridPanel.setLayout(new java.awt.GridLayout(boardMatrix.length, boardMatrix[0].length));

        int buttonSize = 50; // Adjust this value to change the button size

        for (int[] row : boardMatrix) {
            for (int number : row) {
                JButton button = getjButton(number, buttonSize);
                gridPanel.add(button);
            }
        }
        return gridPanel;
    }


    private JButton getjButton(int number, int buttonSize) {
        JButton button = new JButton();
        button.setText("");
        button.setPreferredSize(new Dimension(buttonSize, buttonSize));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14)); // Adjust font style and size as needed
        button.setBackground(Color.lightGray);
        button.setOpaque(true);
        button.addActionListener(e -> {
            if (number == -1) {
                button.setText("X");
                colorFont(button, number);
                button.setBackground(Color.black);
                button.setOpaque(true);
                loseGame();
            } else {
                ArrayList<Integer> cellKey = new ArrayList<>();
                int index = grid.getComponentZOrder(button);
                int row = index / board.getBoardMatrix()[0].length;
                int col = index % board.getBoardMatrix()[0].length;
                cellKey.add(row);
                cellKey.add(col);
                revealedCells.put(cellKey, String.valueOf(number));

                if (number == 0) revealNeighbors(row, col);
//                System.out.println(getRevealedCells());
                button.setBackground(Color.white);
                button.setOpaque(true);
                colorFont(button, number);
                isGameWon();
            }
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    placeFlag(button);
                }
            }
        });

        return button;
    }
    private void addFaceLabelMouseListener() {
        faceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Handle left-click event on the face icon
                    restartGame();
                }
            }
        });
    }
    private Icon getFaceIcon(int gameStatus) {
        return switch (gameStatus) {
            case 0 -> new ImageIcon("../neutral_face.png"); // Neutral face for game in progress
            case 1 -> new ImageIcon("../win_face.png"); // Happy face for game won
            case -1 -> new ImageIcon("../loss_face.png"); // Sad face for game lost
            default -> null;
        };
    }

    private void revealNeighbors(int row, int col) {
        int[][] boardMatrix = this.board.getBoardMatrix();
        int rows = boardMatrix.length;
        int cols = boardMatrix[0].length;

        ArrayList<int[]> neighbors = new ArrayList<>();
        neighbors.add(new int[]{row, col});
        for (int i = 0; i < neighbors.size(); i++) {
            int[] neighbor = neighbors.get(i);
            int r = neighbor[0];
            int c = neighbor[1];
            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                continue;
            }
            JButton button = (JButton) grid.getComponent(r * cols + c);
            if (button.getText().equals("\uD83D\uDEA9")) {
                continue;
            }
            button.setBackground(Color.white);
            button.setOpaque(true);
            if (button.getText().isEmpty()) {
                int number = boardMatrix[r][c];
                ArrayList<Integer> cellKey = new ArrayList<>();
                int index = grid.getComponentZOrder(button);
                int matrixRow = index / board.getBoardMatrix()[0].length;
                int matrixCol = index % board.getBoardMatrix()[0].length;
                neighbors.add(new int[]{matrixRow, matrixCol});
                cellKey.add(matrixRow);
                cellKey.add(matrixCol);
                revealedCells.put(cellKey, String.valueOf(number));
                if (number == 0) {
//                    button.setText(String.valueOf(number));
                    colorFont(button, number);
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            if (dr == 0 && dc == 0) {
                                continue;
                            }
                            neighbors.add(new int[]{r + dr, c + dc});
                        }
                    }
                } else {
                    colorFont(button, number);
                }
            }
        }
    }

    private void loseGame() {
        gameStatus = -1;
        updateHeader();
        disableAllButtons();
    }

    private void isGameWon() {
        int[][] boardMatrix = this.board.getBoardMatrix();
        int rows = boardMatrix.length;
        int cols = boardMatrix[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton button = (JButton) grid.getComponent(r * cols + c);
                int number = boardMatrix[r][c];
                if (number != -1 && button.getText().isEmpty()) {
                    // There are still non-mine cells not revealed
                    return;
                }
            }
        }

        // All non-mine cells have been revealed
        gameStatus = 1;
        updateHeader();
        disableAllButtons();
    }

    private void disableAllButtons() {
        for (int i = 0; i < grid.getComponentCount(); i++) {
            JButton button = (JButton) grid.getComponent(i);
            // Remove the action listener to prevent further clicks
            button.removeActionListener(button.getActionListeners()[0]);
            int boardLength = this.board.getBoardMatrix().length;
            int boardWidth = this.board.getBoardMatrix()[0].length;
            int number = this.board.getBoardMatrix()[i / boardWidth][i % boardLength];
            if (number == -1) {
                colorFont(button);
            } else {
                colorFont(button, number);
            }
        }
    }
    private void placeFlag(JButton button) {
        placeFlagHelper(button);
    }

    public void placeFlag(int row, int col) {
        int[][] boardMatrix = this.board.getBoardMatrix();
        JButton button = (JButton) grid.getComponent(row * boardMatrix[0].length + col);
        placeFlagHelper(button);
    }

    private void placeFlagHelper(JButton button) {
        if (gameStatus != 0) {
            return;
        }
        if (button.getText().isEmpty() && flags > 0) {
            button.setText("\uD83D\uDEA9");
            flags--;
            button.setEnabled(false);
            updateHeader();
        } else if (button.getText().equals("\uD83D\uDEA9")) {
            button.setText("");
            button.setEnabled(true);
            flags++;
            updateHeader();
        }
    }

    public boolean isFlagged(int row, int col) {
        int[][] boardMatrix = this.board.getBoardMatrix();
        JButton button = (JButton) grid.getComponent(row * boardMatrix[0].length + col);
        return button.getText().equals("\uD83D\uDEA9");
    }

    public void revealCell(int row, int col) {
        int[][] boardMatrix = this.board.getBoardMatrix();
        JButton button = (JButton) grid.getComponent(row * boardMatrix[0].length + col);
        button.doClick();
        System.out.println("Revealed cell at row " + row + ", column " + col);
    }

    private void colorFont(JButton button, int number) {
        button.setText(String.valueOf(number));
        switch (number){
            case 0 -> button.setForeground(Color.WHITE);
            case 1 -> button.setForeground(Color.black);
            case 2 -> button.setForeground(Color.PINK);
            case 3 -> button.setForeground(Color.orange);
            default -> button.setForeground(Color.red);
        }
    }
    private void colorFont(JButton button) {
        button.setText("X");
        button.setForeground(Color.red);
    }

    private void updateHeader() {
        // Update the flag label
        JLabel flagLabel = (JLabel) ((JPanel) ((JFrame) SwingUtilities.getWindowAncestor(grid)).getContentPane().getComponent(0)).getComponent(0);
        flagLabel.setText("Flags: " + flags);

        // Update the face label
        JLabel faceLabel = (JLabel) ((JPanel) ((JFrame) SwingUtilities.getWindowAncestor(grid)).getContentPane().getComponent(0)).getComponent(2);
        faceLabel.setIcon(getFaceIcon(gameStatus));
    }

    private void restartGame() {
        // Reset the game variables and board
        gameStatus = 0;
        flags = board.getNumberOfMines();
        revealedCells.clear();

        // Clear the grid panel
        grid.removeAll();

        // Repopulate the grid panel with new buttons
        JPanel newGridPanel = getjPanel();
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(grid);
        frame.remove(grid);
        frame.add(newGridPanel, BorderLayout.CENTER);
        grid = newGridPanel;

        // Update the header components
        updateHeader();

        // Repack the frame to adjust the size
        frame.pack();
    }

    public HashMap<ArrayList<Integer>, String> getRevealedCells() {
        return revealedCells;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public String getDifficulty() {
        return Difficulty;
    }

    public ArrayList<ArrayList<Integer>> getUnrevealedCells() {
        ArrayList<ArrayList<Integer>> unrevealedCells = new ArrayList<>();
        int[][] boardMatrix = this.board.getBoardMatrix();
        for (int row = 0; row < boardMatrix.length; row++) {
            for (int column = 0; column < boardMatrix[0].length; column++) {
                JButton button = (JButton) grid.getComponent(row * boardMatrix[0].length + column);
                if (button.getText().isEmpty()) {
                    unrevealedCells.add(new ArrayList<>(List.of(row, column)));
                }
            }
        }
        return unrevealedCells;
    }

    public ArrayList<ArrayList<Integer>> getNeighboringCells(ArrayList<Integer> cell) {
        ArrayList<ArrayList<Integer>> neighboringCells = new ArrayList<>();
        int row = cell.get(0);
        int column = cell.get(1);
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int newRow = row + dr;
                int newColumn = column + dc;
                if (newRow < 0 || newRow >= board.getBoardMatrix().length || newColumn < 0 || newColumn >= board.getBoardMatrix()[0].length) {
                    continue;
                }
                neighboringCells.add(new ArrayList<>(List.of(newRow, newColumn)));
            }
        }
        return neighboringCells;
    }

    public ArrayList<ArrayList<Integer>> getNeighboringCells(int row, int column) {
        ArrayList<ArrayList<Integer>> neighboringCells = new ArrayList<>();
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int newRow = row + dr;
                int newColumn = column + dc;
                if (newRow < 0 || newRow >= board.getBoardMatrix().length || newColumn < 0 || newColumn >= board.getBoardMatrix()[0].length) {
                    continue;
                }
                neighboringCells.add(new ArrayList<>(List.of(newRow, newColumn)));
            }
        }
        return neighboringCells;
    }

    //TODO: WHEN DIFFICULTY IS CHANGED, THE BOARD SHOULD BE RESTARTED AND SIZE SHOULD BE CHANGED
    public void setDifficulty(String difficulty) {
        Difficulty = difficulty;
    }

}
