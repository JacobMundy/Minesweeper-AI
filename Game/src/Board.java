import java.util.List;

public class Board {
    private final int[][] boardMatrix;
    private final int numberOfMines;
    private List<int[]> mineLocations;

    public Board(int boardLength, int boardWidth, int numberOfMines) {
        this.boardMatrix = new int[boardLength][boardWidth];
        this.numberOfMines = numberOfMines;
        this.mineLocations = new java.util.ArrayList<>();


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

    public int getNumberOfMines() {
        return this.numberOfMines;
    }

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
