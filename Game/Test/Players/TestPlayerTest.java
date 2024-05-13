package Players;

import Game.DisplayBoard;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class TestPlayerTest {

    @Test
    void testPlayer() {
        DisplayBoard displayBoard = new DisplayBoard();
        TestPlayer testPlayer = new TestPlayer(displayBoard);
        ArrayList<int[]> testGetMoves = new ArrayList<>();
        assertEquals(testGetMoves, testPlayer.getMoves());
        testPlayer.play();
        testGetMoves.add(new int[]{0, 0});
        assertArrayEquals(testGetMoves.get(0), testPlayer.getMoves().get(0));


    }

}