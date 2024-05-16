package Game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DisplayBoardTest {

    @Test
    void gettersAndSetters(){
        DisplayBoard displayBoard = new DisplayBoard("Medium");
        assertEquals("Medium", displayBoard.getDifficulty());
        assertEquals(0, displayBoard.getGameStatus());
        displayBoard.setDifficulty("Hard");
        assertEquals("Hard", displayBoard.getDifficulty());
        displayBoard.placeFlag(0, 0);
     }

    @Test
    void displayGUI() throws InterruptedException {
        DisplayBoard displayBoard = new DisplayBoard();
        Thread guiThread = new Thread(displayBoard::displayGUI);
        guiThread.start();
//        guiThread.join();
    }
}