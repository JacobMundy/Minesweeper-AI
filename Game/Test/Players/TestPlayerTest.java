package Players;

import Game.DisplayBoard;
import org.junit.jupiter.api.Test;


class TestPlayerTest {

    @Test
    void testPlayer() throws InterruptedException {
        DisplayBoard displayBoard = new DisplayBoard();

        // Create a new thread for the displayGUI method
        Thread guiThread = new Thread(displayBoard::displayGUI);
        guiThread.start();

        Thread.sleep(1000);
        // Create a new thread for the player's actions
        Thread playerThread = new Thread(() -> {
            TestPlayer testPlayer = new TestPlayer(displayBoard);
            testPlayer.play();
        });
        playerThread.start();

        // Wait for the threads to finish
        guiThread.join();
        playerThread.join();
    }

}