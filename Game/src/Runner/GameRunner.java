package Runner;

import Game.DisplayBoard;
import Players.Player;
import Players.RandomPlayer;

public class GameRunner {
    private DisplayBoard gameBoard;
    private Player player;
    public GameRunner() {
        System.out.println("Creating a new Game");
        this.gameBoard = new DisplayBoard();

        //TODO: Change when new players are implemented
        System.out.println("Creating a new RandomPlayer");
        this.player = new RandomPlayer(gameBoard);
    }

    public void startGame(boolean DisplayGame) throws InterruptedException {
        Thread playerThread = new Thread(player::play);
        if (DisplayGame) {
            System.out.println("Starting the game, displaying the GUI");
            Thread displayThread = new Thread(gameBoard::displayGUI);
            displayThread.start();

            // Waits for the GUI to be displayed before starting the player
            // Also joins the displayThread so the window does not close when the player is done
            Thread.sleep(1000);
            playerThread.start();
            displayThread.join();

        } else {
            System.out.println("Starting the game without displaying the GUI");
            playerThread.start();
        }

    }
}
