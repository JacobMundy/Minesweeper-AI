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
    }

    @Test
    void displayGUI() {
        DisplayBoard displayBoard = new DisplayBoard();
        Thread guiThread = new Thread(displayBoard::displayGUI);

    }
}