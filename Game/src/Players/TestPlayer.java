package Players;

import Game.DisplayBoard;

public class TestPlayer extends Player {
    private DisplayBoard gameBoard;

    public TestPlayer(DisplayBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    @Override
    public void play() {
        System.out.println("Playing the game");
        makeMove();
    }

    protected void makeMove() {
        System.out.println("Making a move");
        moves.add(new int[]{0, 0});
    }


}