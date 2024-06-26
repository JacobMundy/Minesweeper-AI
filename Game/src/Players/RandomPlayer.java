package Players;

import Game.DisplayBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * The type Random player.
 */
public class RandomPlayer extends Player {
    private DisplayBoard gameBoard;
    private HashMap<ArrayList<Integer>, String> revealedCells = new HashMap<>();

    /**
     * Instantiates a new Random player.
     *
     * @param gameBoard the game board you want to play on
     */
    public RandomPlayer(DisplayBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Runs the players logic.
     * Recommended to run this in a new thread if
     * you are displaying the games GUI.
     */
    @Override
    public void play() {
        System.out.println("Playing the game");
        gameBoard.revealCell(0, 0);
        moves.add(new int[]{0, 0});
        makeMove();
    }

    protected void makeMove() {
        ArrayList<ArrayList<Integer>> unrevealedCells = gameBoard.getUnrevealedCells();
        while (!unrevealedCells.isEmpty()) {
            ArrayList<Integer> randomCell = getRandomUnrevealedCell();
            int row = randomCell.get(0);
            int col = randomCell.get(1);

            gameBoard.revealCell(row, col);
            moves.add(new int[]{row, col});
            revealedCells.putAll(gameBoard.getRevealedCells());
            unrevealedCells.removeAll(revealedCells.keySet());

            // Check if the game is over
            if (gameBoard.getGameStatus() == 1 || gameBoard.getGameStatus() == -1) {
                break;
            }
        }

    }
    private ArrayList<Integer> getRandomUnrevealedCell() {
        Random random = new Random();
        ArrayList<ArrayList<Integer>> unrevealedCells = gameBoard.getUnrevealedCells();
        int randomIndex = random.nextInt(unrevealedCells.size());
        return unrevealedCells.get(randomIndex);
    }

}