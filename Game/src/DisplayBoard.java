import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class DisplayBoard {

    private String Difficulty;
    private Board board;

    // 0 = game in progress, 1 = game won, -1 = game lost
    private int gameStatus = 0;

    private JPanel grid = new JPanel();
    private int flags;

    public DisplayBoard(String Difficulty) {
        this.Difficulty = Difficulty;
        switch (Difficulty) {
            case "Medium" -> this.board = new Board(16, 16, 40);
            case "Hard" -> this.board = new Board(16, 30, 99);
            default -> this.board = new Board(9, 9, 10);
        }
        this.flags = this.board.getNumberOfMines();
        displayGUI();
    }
    public DisplayBoard() {
        this("Easy");
    }


    public void displayGUI() {
        JPanel gridPanel = getjPanel();

        // Create the header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel flagLabel = new JLabel("Flags: " + flags);
        JLabel mineLabel = new JLabel("Mines: " + board.getNumberOfMines());
        JLabel faceLabel = new JLabel(getFaceIcon(gameStatus));
        headerPanel.add(flagLabel, BorderLayout.WEST);
        headerPanel.add(mineLabel, BorderLayout.EAST);
        headerPanel.add(faceLabel, BorderLayout.CENTER);

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
        frame.pack();
        frame.setVisible(true);

        // Wait for the JFrame to be closed
        while (frame.isVisible()) {
            try {
                Thread.sleep(100); // Sleep for a short time to avoid excessive CPU usage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel getjPanel() {
        int[][] boardMatrix = this.board.getBoardMatrix();
        JPanel gridPanel = this.grid;
        gridPanel.setLayout(new java.awt.GridLayout(boardMatrix.length, boardMatrix[0].length));

        int buttonSize = 45; // Adjust this value to change the button size

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


        button.addActionListener(e -> {
            if (number == -1) {
                button.setText("X");
                colorFont(button, number);
                button.setBackground(Color.black);
                button.setOpaque(true);
                loseGame();
            } else if (number == 0) {
                int index = grid.getComponentZOrder(button);
                int row = index / board.getBoardMatrix()[0].length;
                int col = index % board.getBoardMatrix()[0].length;
                revealNeighbors(row, col);
                colorFont(button, number);
            }
            else {
                colorFont(button, number);
            }
        });
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    placeFlag(button); // Call the placeFlag function
                }
            }

            // Implement the other required methods from MouseListener
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        return button;
    }
    private Icon getFaceIcon(int gameStatus) {
        return switch (gameStatus) {
            case 0 -> new ImageIcon("neutral_face.png"); // Neutral face for game in progress
            case 1 -> new ImageIcon("win_face.png"); // Happy face for game won
            case -1 -> new ImageIcon("loss_face.png"); // Sad face for game lost
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
            if (button.getText().isEmpty()) {
                int number = boardMatrix[r][c];
                if (number == 0) {
                    button.setText(String.valueOf(number));
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
        for (int i = 0; i < grid.getComponentCount(); i++) {
            JButton button = (JButton) grid.getComponent(i);
            int number = this.board.getBoardMatrix()[i / 9][i % 9];
            if (number == -1) {
                colorFont(button);
            } else {
                colorFont(button, number);
            }
        }
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
    }

    public void placeFlag(JButton button) {
        if (gameStatus != 0 || flags == 0) {
            return;
        }
        if (button.getText().isEmpty()) {
            button.setText("F");
            button.removeActionListener(button.getActionListeners()[0]);
            flags--;
        } else if (button.getText().equals("F")) {
            button.setText("");
            flags++;
            System.out.println(button.getAlignmentX());
            updateHeader();
        }
    }

    private void colorFont(JButton button, int number) {
        button.setText(String.valueOf(number));
        switch (number){
            case 0 -> button.setForeground(Color.blue);
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
        System.out.println(getFaceIcon(gameStatus));
    }
}
