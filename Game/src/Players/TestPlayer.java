package Players;

import Game.DisplayBoard;


public class TestPlayer {
    private DisplayBoard gameBoard;

    public TestPlayer(DisplayBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void play() {
        System.out.println("Playing the game");
        makeMove();
    }

    private void makeMove() {
        System.out.println("Making a move");
        gameBoard.revealCell(0, 0);
    }
}