package Runner;

import Game.DisplayBoard;
import Players.*;

public class GameRunner {
    private DisplayBoard gameBoard;
    private Player player;
    public GameRunner(String playerName) {
        System.out.println("Creating a new Game");
        this.gameBoard = new DisplayBoard();

        System.out.println("Creating a new Player");
        switch (playerName) {
            case "RandomPlayer" ->
                this.player = new RandomPlayer(gameBoard);
            case "StrategyPlayer" ->
                this.player = new StrategyPlayer(gameBoard);
            default -> {
                System.out.println("Invalid player name, defaulting to RandomPlayer");
                this.player = new RandomPlayer(gameBoard);
            }
        }
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