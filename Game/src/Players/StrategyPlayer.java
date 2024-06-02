package Players;

import Game.DisplayBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class StrategyPlayer extends Player {

    private DisplayBoard gameBoard;
    private HashMap<ArrayList<Integer>, String> revealedCells = new HashMap<>();
    private ArrayList<ArrayList<Integer>> checkedCells = new ArrayList<>();

    public StrategyPlayer(DisplayBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    @Override
    public void play() {
        System.out.println("Playing the game");
        gameBoard.revealCell(0, 0);
        moves.add(new int[]{0, 0});
        revealedCells = gameBoard.getRevealedCells();
        makeMove();
    }

    @Override
    protected void makeMove() {
        System.out.println("Making a move");
        while (gameBoard.getGameStatus() == 0) {

            revealedCells = gameBoard.getRevealedCells();
            boolean madeMove = false;

            for (ArrayList<Integer> cell : new ArrayList<>(revealedCells.keySet())) {
                if (!checkedCells.contains(cell)) {
                    int neighboringMines = Integer.parseInt(revealedCells.get(cell));
                    int row = cell.get(0);
                    int col = cell.get(1);
                    ArrayList<ArrayList<Integer>> neighboringCells = gameBoard.getNeighboringCells(row, col);
                    checkedCells.add(cell);
                    madeMove = checkNeighborList(neighboringCells, neighboringMines) || madeMove;

                    if (gameBoard.getGameStatus() != 0) {
                        break;
                    }
                }
            }

//          If no move was made, break to avoid infinite loop
            if (!madeMove) {
                break;
            }

            revealedCells = gameBoard.getRevealedCells(); // Update revealedCells map after each iteration
        }
        if (gameBoard.getGameStatus() == 0) {
            ArrayList<Integer> randomCell = getRandomUnrevealedCell();
            int row = randomCell.get(0);
            int col = randomCell.get(1);
            gameBoard.revealCell(row, col);
            moves.add(new int[]{row, col});
            makeMove();
        } else {
            System.out.println("Game Over");
        }
    }

    private boolean checkNeighborList(ArrayList<ArrayList<Integer>> neighboringCells, int neighboringMines) {
        int unrevealedCells = 0;
        int flaggedCells = 0;
        int revealedCells = 0;
        boolean madeMove = false;

        for (ArrayList<Integer> cell : neighboringCells) {
            if (this.revealedCells.containsKey(cell) || checkedCells.contains(cell)) {
                revealedCells++;
                continue;
            }
            unrevealedCells++;
            if (gameBoard.isFlagged(cell.get(0), cell.get(1))) {
                flaggedCells++;
            }
        }

        if (unrevealedCells == neighboringMines - flaggedCells) {
            for (ArrayList<Integer> cell : neighboringCells) {
                if (this.revealedCells.containsKey(cell) || gameBoard.isFlagged(cell.get(0), cell.get(1))) {
                    continue;
                }
                gameBoard.placeFlag(cell.get(0), cell.get(1));
                moves.add(new int[]{cell.get(0), cell.get(1)});
                madeMove = true;
            }
        } else if (neighboringMines == flaggedCells){
            for (ArrayList<Integer> cell : neighboringCells) {
                if (this.revealedCells.containsKey(cell) || gameBoard.isFlagged(cell.get(0), cell.get(1))) {
                    continue;
                }
                gameBoard.revealCell(cell.get(0), cell.get(1));
                moves.add(new int[]{cell.get(0), cell.get(1)});
                madeMove = true;
            }
        } else if (revealedCells == neighboringMines) {
            for (ArrayList<Integer> cell : neighboringCells) {
                if (this.revealedCells.containsKey(cell) || gameBoard.isFlagged(cell.get(0), cell.get(1)) ) {
                    continue;
                }
                gameBoard.placeFlag(cell.get(0), cell.get(1));
                moves.add(new int[]{cell.get(0), cell.get(1)});
                madeMove = true;
            }
        }

        return madeMove;
    }

    private ArrayList<Integer> getRandomUnrevealedCell() {
        Random random = new Random();
        ArrayList<ArrayList<Integer>> unrevealedCells = gameBoard.getUnrevealedCells();
        int randomIndex = random.nextInt(unrevealedCells.size());
        return unrevealedCells.get(randomIndex);
    }

    private ArrayList<Integer> getRandomCheckedCell() {
        Random random = new Random();
        int randomIndex = random.nextInt(checkedCells.size());
        return checkedCells.get(randomIndex);
    }
}
