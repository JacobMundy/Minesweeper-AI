package Runner;

import Game.DisplayBoard;
import Players.Player;
import Players.TestPlayer;

public class GameRunner {
    private DisplayBoard gameBoard;
    private Player player;
    public GameRunner() {
        System.out.println("Creating a new Game");
        this.gameBoard = new DisplayBoard();

        //TODO: Change when new players are implemented
        System.out.println("Creating a new TestPlayer");
        this.player = new TestPlayer(gameBoard);
    }

    public void startGame(boolean DisplayGame) throws InterruptedException {
        if (DisplayGame) {
            System.out.println("Starting the game, displaying the GUI");
            Thread displayThread = new Thread(gameBoard::displayGUI);
            displayThread.start();

            // Waits for the GUI to be displayed before starting the player
            Thread.sleep(1000);
        }
        Thread playerThread = new Thread(player::play);
        playerThread.start();
    }
}
