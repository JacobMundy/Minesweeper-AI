package Game;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Scanner;

/**
 * The type Display board.
 */
public class DisplayBoard {
    // Info about the board
    private String Difficulty;
    private Board board;
    private HashMap<ArrayList<Integer>, String> revealedCells = new HashMap<>();
    private ArrayList<Integer> boardInfo = new ArrayList<>();
    private boolean firstClick = true;
    private boolean statsShown = false;

    // 0 = game in progress, 1 = game won, -1 = game lost
    private int gameStatus = 0;

    // Components for the GUI
    private JPanel grid = new JPanel();
    private JFrame frame = new JFrame();
    private JFrame statsFrame = new JFrame();

    // Labels and variables for the header
    private int flags;
    private JLabel faceLabel;
    private JLabel flagLabel;
    private JLabel mineLabel;
    private JLabel timeLabel;
    private Timer timer;
    private int elapsedTime = 0;
    private JPanel pauseMenu;

    /**
     * Instantiates a new Display board on specified difficulty.
     *
     * @param Difficulty the game difficulty (Easy, Medium, Hard)
     */
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

    /**
     * Instantiates a new Display board on easy mode.
     */
    public DisplayBoard() {
        this("Easy");
    }


    /**
     * Displays the User Interface
     * Will  urunntil the JFrame is closed.
     */
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


    private void initializeGame(){
        JPanel gridPanel = getjPanel();

        // Create the header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        flagLabel = new JLabel("Flags: " + flags);
        mineLabel = new JLabel("Mines: " + board.getNumberOfMines());
        timeLabel = new JLabel("Time: 0");
        JPanel timeAndFlagPanel = new JPanel();
        timeAndFlagPanel.setLayout(new GridLayout(2,1));
        timeAndFlagPanel.add(flagLabel);
        timeAndFlagPanel.add(timeLabel);
        headerPanel.add(timeAndFlagPanel, BorderLayout.WEST);
        this.faceLabel = new JLabel(getFaceIcon(gameStatus));
        faceLabel.setPreferredSize(new Dimension(100, 100));
        addFaceLabelMouseListener();
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
                System.exit(0); // Exit the program
            }
        });
        statsFrame.setIconImage(new ImageIcon("icons/info.png").getImage());
        statsFrame.setTitle("Player Stats");

        frame.setIconImage(new ImageIcon("icons/mine.png").getImage());
        frame.setTitle("Minesweeper");


        this.frame = frame;

        timer = new Timer(1000, e -> {
            elapsedTime++;
            timeLabel.setText("Time: " + elapsedTime);
        });
    }

    private JPanel getjPanel() {
        int[][] boardMatrix = this.board.getBoardMatrix();
        JPanel gridPanel = this.grid;
        gridPanel.setLayout(new GridLayout(boardMatrix.length, boardMatrix[0].length));

        int buttonSize = 25;

        for (int[] row : boardMatrix) {
            for (int number : row) {
                JButton button = getjButton(number, buttonSize);
                gridPanel.add(button);
            }
        }
        return gridPanel;
    }


    private JButton getjButton(int initialNumber, int buttonSize) {
        CustomButton button = new CustomButton();
        AtomicReference<Integer> number = new AtomicReference<>(initialNumber);

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
            if (!timer.isRunning()) timer.start();
            // In addition to the first click check, we also need a new function (handleFirstClick)
            // due to swing threading issues causing the cell to not be revealed
            if (firstClick && number.get() == -1){
                firstClick = false;
                int index = grid.getComponentZOrder(button);
                int row = index / board.getBoardMatrix()[0].length;
                int col = index % board.getBoardMatrix()[0].length;
                SwingUtilities.invokeLater(() -> handleFirstClick(row, col, button, number));
            }
            else if (number.get() == -1) {
                button.setText("X");
                colorFont(button, initialNumber);
                button.setBackground(Color.black);
                button.setOpaque(true);
                loseGame();
            } else {
                // Any cell that isn't a mine
                firstClick = false;
                ArrayList<Integer> cellKey = new ArrayList<>();

                // Save the cell key to the revealedCells map (used by heuristic players)
                int index = grid.getComponentZOrder(button);
                int row = index / board.getBoardMatrix()[0].length;
                int col = index % board.getBoardMatrix()[0].length;
                cellKey.add(row);
                cellKey.add(col);
                revealedCells.put(cellKey, String.valueOf(initialNumber));

                // Reveal the cell
                if (number.get() == 0) revealNeighbors(row, col);
                button.setBackground(Color.white);
                button.setOpaque(true);
                colorFont(button, initialNumber);
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

    /**
     * Regenerates the board with a new safe cell.
     * (used when the first click is a mine)
     *
     * @param safeRow    the safe cell row
     * @param safeColumn the safe cell column
     */
    public void regenerateBoard(int safeRow, int safeColumn) {
        board.generateSafeBoard(safeRow, safeColumn);
        resetGrid();
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


    private void handleFirstClick(int row, int col, JButton button, AtomicReference<Integer> number) {
        regenerateBoard(row, col);
        int newValue = board.getBoardMatrix()[row][col];
        number.set(newValue); // Update the number with the new value
        button.setText(String.valueOf(newValue)); // Reveal the button manually
        colorFont(button, newValue); // Color the font based on the new value
        button.setBackground(Color.white);
        button.setOpaque(true);
        ArrayList<Integer> cellKey = new ArrayList<>();
        cellKey.add(row);
        cellKey.add(col);
        revealedCells.put(cellKey, String.valueOf(newValue));
        revealNeighbors(row, col);
        isGameWon();

    }

    private Icon getFaceIcon(int gameStatus) {
        return switch (gameStatus) {
            case 0 -> new ImageIcon("icons/neutral_face.png"); // Neutral face for game in progress
            case 1 -> new ImageIcon("icons/win_face.png"); // Happy face for game won
            case -1 -> new ImageIcon("icons/loss_face.png"); // Sad face for game lost
            default -> null;
        };
    }

    /**
     * Toggles pause menu.
     */
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
        timer.stop();
        gameStatus = -1;
        updateHeader();
        disableAllButtons();
        saveStats(false, elapsedTime, Difficulty);
        updateStats();
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
        timer.stop();
        gameStatus = 1;
        updateHeader();
        disableAllButtons();
        saveStats(true, elapsedTime, Difficulty);
        updateStats();
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

    /**
     * Flag cell.
     *
     * @param row the cell row
     * @param col the cell column
     */
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

    /**
     * Returns whether specified cell is flagged.
     *
     * @param row the cell row
     * @param col the cell column
     * @return boolean
     */
    public boolean isFlagged(int row, int col) {
        int[][] boardMatrix = this.board.getBoardMatrix();
        JButton button = (JButton) grid.getComponent(row * boardMatrix[0].length + col);
        return button.getText().equals("\uD83D\uDEA9");
    }

    /**
     * Reveals specified cell.
     *
     * @param row the cell row
     * @param col the cell column
     */
    public void revealCell(int row, int col) {
        int[][] boardMatrix = this.board.getBoardMatrix();
        JButton button = (JButton) grid.getComponent(row * boardMatrix[0].length + col);
        button.doClick();
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
        mineLabel.setText("Mines: " + board.getNumberOfMines());
        timeLabel.setText("Time: " + elapsedTime);

        // Update the face label
        faceLabel.setIcon(getFaceIcon(gameStatus));
    }

    /**
     * Restart game.
     */
    public void restartGame() {
        // Reset the game variables and board
        timer.stop();
        elapsedTime = 0;
        gameStatus = 0;
        board = new Board(boardInfo.get(0), boardInfo.get(1), boardInfo.get(2));
        flags = board.getNumberOfMines();
        revealedCells.clear();
        firstClick = true;

        // Clear the grid panel
        resetGrid();
    }


    private void resetGrid() {
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

    private void saveStats(Boolean gameWon, int timeElapsed, String difficulty) {
        File file = new File("stats.txt");
        try {
            boolean fileCreated = file.createNewFile();
            if (fileCreated) System.out.println("File created: " + file.getName());

            FileWriter writer = new FileWriter(file, true);
            writer.append(gameWon ? "won " : "lost ").append(String.valueOf(timeElapsed)).append(" ").append(difficulty).append("\n");
            writer.close();
        } catch (Exception e) {
            System.out.println("Error occurred while creating file: " + e.getMessage());
        }
    }

    /**
     * Displays the users stats.
     * (located in stats.txt)
     */
    public void showStats() {
        try {
            File file = new File("stats.txt");
            if (file.createNewFile()) System.out.println("File created: " + file.getName());
            // If the stats frame is already open, remove all components
            // for a fresh start otherwise reset the frame size and location
            if (statsShown) {
                statsFrame.getContentPane().removeAll();
            } else {
                statsFrame.setSize(400, 400);
                statsFrame.setLocationRelativeTo(frame);
            }

            statsShown = true;

            // Set the closing behavior and add a window listener
            statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            statsFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    statsShown = false;
                }
            });

            statsFrame.setTitle("Game Stats");
            statsFrame.setLayout(new BorderLayout());

            String[] columnNames = {"Game Result", "Time Elapsed", "Difficulty"};
            List<String[]> data = new ArrayList<>();

            int totalGames = 0;
            int totalWins = 0;

            Scanner scanner = new java.util.Scanner(file).useDelimiter("\\A");
            String fileContent = scanner.hasNext() ? scanner.next() : "";
            for (String line : fileContent.split("\n")) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] splitLine = line.split(" ");
                String gameResult = splitLine[0];
                String timeElapsed = splitLine[1];
                String difficulty = splitLine[2];
                data.add(new String[]{gameResult, timeElapsed, difficulty});

                totalGames++;
                if (gameResult.equals("won")) {
                    totalWins++;
                }
            }
            // Calculate the win percentage and display the total games and wins
            double winPercentage = (double) totalWins / totalGames * 100;

            JLabel totalGamesLabel = new JLabel("Total games: " + totalGames);
            statsFrame.add(totalGamesLabel, BorderLayout.NORTH);

            JLabel totalWinsLabel = new JLabel("Total wins: " + totalWins + " (" + winPercentage + "%)");

            JTable statsTable = new JTable(data.toArray(new String[0][]), columnNames);
            JScrollPane scrollPane = new JScrollPane(statsTable);
            statsFrame.add(scrollPane, BorderLayout.CENTER);

            // Add a button to clear the stats
            JButton clearStatsButton = new JButton("Clear Stats");
            clearStatsButton.addActionListener(e -> {
                try {
                    FileWriter writer = new FileWriter(file);
                    writer.write("");
                    writer.close();
                    statsShown = false;
                    statsFrame.getContentPane().removeAll();
                    statsFrame.dispose();
                } catch (Exception ex) {
                    System.out.println("Error occurred while clearing stats: " + ex.getMessage());
                }
            });

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.add(totalWinsLabel);
            bottomPanel.add(clearStatsButton);
            statsFrame.add(bottomPanel, BorderLayout.SOUTH);
            statsFrame.setVisible(true);

        } catch (Exception e) {
            System.out.println("Error occurred while opening file: " + e.getMessage());
        }
    }

    /**
     * Refreshes the stats frame.
     */
    public void updateStats() {
        if (statsShown) {
            statsFrame.getContentPane().removeAll();
            showStats();
            frame.setVisible(true);
        }
    }

    private void setBoardInfo(int rowSize, int columnSize, int mines) {
        this.boardInfo.add(rowSize);
        this.boardInfo.add(columnSize);
        this.boardInfo.add(mines);
    }

    /**
     * Gets revealed cells.
     *
     * @return HashMap of revealed cells
     */
    public HashMap<ArrayList<Integer>, String> getRevealedCells() {
        return revealedCells;
    }

    /**
     * Gets game status.
     *
     * @return the game status (0 = game in progress, 1 = game won, -1 = game lost)
     */
    public int getGameStatus() {
        return gameStatus;
    }

    /**
     * Gets difficulty.
     *
     * @return the difficulty
     */
    public String getDifficulty() {
        return Difficulty;
    }

    /**
     * Gets board info.
     *
     * @return the board info
     */
    public ArrayList<Integer> getBoardInfo() {
        return boardInfo;
    }

    /**
     * Gets unrevealed cells.
     *
     * @return ArrayList of row column pairs
     */
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

    /**
     * Gets neighboring cells.
     * (calls the other getNeighboringCells method)
     *
     * @param cell List in format of [row, column]
     * @return List of neighboring cells
     */
    public ArrayList<ArrayList<Integer>> getNeighboringCells(ArrayList<Integer> cell) {
        int row = cell.get(0);
        int column = cell.get(1);
        return getNeighboringCells(row, column);
    }

    /**
     * Gets neighboring cells.
     *
     * @param row    the cell row
     * @param column the cell column
     * @return List of neighboring cells
     */
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


    /**
     * Adds a key binding to the JFrame.
     * (used by heuristic players)
     *
     * @param name     Event name
     * @param keyEvent the key event
     * @param action   the action
     */
    public void addKeyBinding(String name, int keyEvent, Action action) {
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(keyEvent, 0), name);
        actionMap.put(name, action);
    }


    /**
     * Sets difficulty.
     * And restarts the game with the new difficulty.
     *
     * @param difficulty String value of Difficulty
     */
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

/**
 * This class is a custom JButton that does not truncate text.
 */
class CustomButton extends JButton {

    /**
     * Instantiates the CustomButton.
     * (Essentially a JButton)
     */
    public CustomButton() {
        super();
        setUI(new CustomButtonUI());
    }

    // Paints the text in the center of the button
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
            // If the superclass paint method is called, ellipsis will be added BLOCKING the text
        }
    }
}