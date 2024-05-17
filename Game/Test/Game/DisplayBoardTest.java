package Game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
    void displayGUI() {
        DisplayBoard displayBoard = new DisplayBoard();
        Thread guiThread = new Thread(displayBoard::displayGUI);
        guiThread.start();

//        try {
//            guiThread.join();
//        } catch (InterruptedException e) {
//            System.out.println("Thread interrupted");
//        }
    }

    @Test
    void getNeighboringCells(){
        DisplayBoard displayBoard = new DisplayBoard();
        System.out.println(displayBoard.getNeighboringCells(0, 0));
        System.out.println(displayBoard.getNeighboringCells(6, 6));
        ArrayList<Integer> cell = new ArrayList<>();
        cell.add(0);
        cell.add(0);
        assertEquals(displayBoard.getNeighboringCells(0, 0), displayBoard.getNeighboringCells(cell));
    }
}