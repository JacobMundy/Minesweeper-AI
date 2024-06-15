package Game;

import java.util.Arrays;
import java.util.List;

/**
 * The representation of the game board.
 */
public class Board {
    private final int[][] boardMatrix;
    private final int numberOfMines;
    private List<int[]> mineLocations;
    private final int boardLength;
    private final int boardWidth;

    /**
     * Instantiates a new Board.
     *
     * @param boardLength   the board length
     * @param boardWidth    the board width
     * @param numberOfMines the number of mines
     */
    public Board(int boardLength, int boardWidth, int numberOfMines) {
        this.boardMatrix = new int[boardLength][boardWidth];
        this.numberOfMines = numberOfMines;
        this.mineLocations = new java.util.ArrayList<>();
        this.boardLength = boardLength;
        this.boardWidth = boardWidth;


        // Initialize the board with mines
        for (int minesPlaced = 0; minesPlaced < numberOfMines; minesPlaced++) {
            int x = (int) (Math.random() * boardLength);
            int y = (int) (Math.random() * boardWidth);
            if (this.boardMatrix[x][y] == -1) {
                minesPlaced--;
                continue;
            }
            this.boardMatrix[x][y] = -1;
            this.mineLocations.add(new int[] {x, y});
        }


        // Initialize the board with numbers
        for (int[] mineLocation : this.mineLocations) {
            int x = mineLocation[0];
            int y = mineLocation[1];
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i < 0 || i >= boardLength || j < 0 || j >= boardWidth) {
                        continue;
                    }
                    if (this.boardMatrix[i][j] == -1) {
                        continue;
                    }
                    this.boardMatrix[i][j]++;
                }
            }
        }

    }

    /**
     * Resets board with safe cell.
     * Does not generate new board changes the current board
     *
     * @param safeRow    the cells row
     * @param safeColumn the cells column
     */
    public void generateSafeBoard(int safeRow, int safeColumn) {
        // Clear the current board
        for (int[] row : this.boardMatrix) {
            Arrays.fill(row, 0);
        }

        // Clear the current mine locations
        this.mineLocations.clear();

        // Generate new mines excluding the safe cell
        for (int minesPlaced = 0; minesPlaced < numberOfMines; minesPlaced++) {
            int x, y;
            do {
                x = (int) (Math.random() * boardLength);
                y = (int) (Math.random() * boardWidth);
            } while (x == safeRow && y == safeColumn || this.boardMatrix[x][y] == -1);

            this.boardMatrix[x][y] = -1;
            this.mineLocations.add(new int[] {x, y});
        }

        // Generate new numbers
        for (int[] mineLocation : this.mineLocations) {
            int x = mineLocation[0];
            int y = mineLocation[1];
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i < 0 || i >= boardLength || j < 0 || j >= boardWidth) {
                        continue;
                    }
                    if (this.boardMatrix[i][j] == -1) {
                        continue;
                    }
                    this.boardMatrix[i][j]++;
                }
            }
        }
    }


    /**
     * Gets number of mines.
     *
     * @return the number of mines
     */
    public int getNumberOfMines() {
        return this.numberOfMines;
    }

    /**
     * Get board matrix
     *
     * @return  board matrix as a 2D array
     */
    public int[][] getBoardMatrix() {
        return this.boardMatrix;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : this.boardMatrix) {
            for (int number : row) {
                sb.append(String.format("%3d", number));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
