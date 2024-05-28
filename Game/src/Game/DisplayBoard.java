package Game;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayBoard {
    private String Difficulty;
    private Board board;
    private HashMap<ArrayList<Integer>, String> revealedCells = new HashMap<>();
    private ArrayList<Integer> boardInfo = new ArrayList<>();
    // 0 = game in progress, 1 = game won, -1 = game lost
    private int gameStatus = 0;

    private JPanel grid = new JPanel();
    private JFrame frame = new JFrame();
    private int flags;
    private JLabel faceLabel;

    private JLabel flagLabel;

    private JPanel pauseMenu;

    public DisplayBoard(String Difficulty) {
        this.Difficulty = Difficulty;
        switch (Difficulty) {
            case "Medium" -> {
                this.board = new Board(16, 16, 40);
                setBoardInfo(16, 16, 40);
            }
            case "Hard" -> {
                this.board = new Board(16, 30, 99);
                setBoardInfo(16, 30, 99);
            }
            default -> {
                this.board = new Board(9, 9, 10);
                setBoardInfo(9, 9, 10);
            }
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
            frame.setLocation(200, 200);

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
        flagLabel = new JLabel("Flags: " + flags);
        JLabel mineLabel = new JLabel("Mines: " + board.getNumberOfMines());
        flagLabel.setPreferredSize(new Dimension(100, 50));
        this.faceLabel = new JLabel(getFaceIcon(gameStatus));
        faceLabel.setPreferredSize(new Dimension(100, 100));
        addFaceLabelMouseListener();
        headerPanel.add(flagLabel, BorderLayout.WEST);
        headerPanel.add(mineLabel, BorderLayout.EAST);
        headerPanel.add(faceLabel, BorderLayout.CENTER);


        // Initialize the pause menu
        pauseMenu = new JPanel();
        pauseMenu.setVisible(true);

        // Initialize the buttons
        JComboBox<String> difficultyComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        JButton restartButton = new JButton("Restart");
        JButton statsButton = new JButton("Stats");
        JButton quitButton = new JButton("Quit");


        // Add action listeners to the buttons
        restartButton.addActionListener(e -> restartGame());
        statsButton.addActionListener(e -> showStats());
        quitButton.addActionListener(e -> System.exit(0));
        difficultyComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedDifficulty = (String) e.getItem();
                setDifficulty(selectedDifficulty);
            }
        });

        // Add the buttons to the pause menu
        pauseMenu.add(difficultyComboBox);
        pauseMenu.add(restartButton);
        pauseMenu.add(statsButton);
        pauseMenu.add(quitButton);


        // Create the JFrame
        JFrame frame = new JFrame();
        frame.add(pauseMenu, BorderLayout.SOUTH);
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "pause");
        frame.getRootPane().getActionMap().put("pause", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle the visibility of the pause menu
                togglePauseMenu();
            }
        });

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
        gridPanel.setLayout(new GridLayout(boardMatrix.length, boardMatrix[0].length));

        int buttonSize = 25; // Adjust this value to change the button size

        for (int[] row : boardMatrix) {
            for (int number : row) {
                JButton button = getjButton(number, buttonSize);
                gridPanel.add(button);
            }
        }
        return gridPanel;
    }


    private JButton getjButton(int number, int buttonSize) {
        CustomButton button = new CustomButton();


        button.setText("");
        button.setPreferredSize(new Dimension(buttonSize, buttonSize));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20)); // Adjust font style and size as needed
        button.setBackground(Color.lightGray);
        button.setOpaque(true);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
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

    public void togglePauseMenu() {
        pauseMenu.setVisible(!pauseMenu.isVisible());
        System.out.println("Pause menu visibility: " + pauseMenu.isVisible());
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
            int number = this.board.getBoardMatrix()[i / board.getBoardMatrix()[0].length][i % board.getBoardMatrix()[0].length];
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
        flagLabel.setText("Flags: " + flags);

        // Update the face label
        faceLabel.setIcon(getFaceIcon(gameStatus));
    }

    private void restartGame() {
        // Reset the game variables and board
        gameStatus = 0;
        board = new Board(boardInfo.get(0), boardInfo.get(1), boardInfo.get(2));
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

    private void showStats() {
        // TODO
        System.out.println("Showing game statistics...");
    }

    private void setBoardInfo(int rowSize, int columnSize, int mines) {
        this.boardInfo.add(rowSize);
        this.boardInfo.add(columnSize);
        this.boardInfo.add(mines);
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

    public ArrayList<Integer> getBoardInfo() {
        return boardInfo;
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
        int row = cell.get(0);
        int column = cell.get(1);
        return getNeighboringCells(row, column);
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


    public void setDifficulty(String difficulty) {
        Difficulty = difficulty;
        this.boardInfo.clear();
        switch (difficulty) {
            case "Medium" -> setBoardInfo(16, 16, 40);
            case "Hard" -> setBoardInfo(16, 30, 99);
            default -> setBoardInfo(9, 9, 10);

        }
        restartGame();
    }

}

// All of this just because JButtons auto truncate text without any way to change it.

class CustomButton extends JButton {
    public CustomButton() {
        super();
        setUI(new CustomButtonUI());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getForeground());
        g.setFont(getFont());
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(getText())) / 2;
        int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(getText(), x, y);
    }

    private static class CustomButtonUI extends MetalButtonUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            // Do nothing here to prevent the UI delegate from painting the text
        }
    }
}