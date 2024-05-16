package Players;

import Game.DisplayBoard;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class TestRandomPlayer {

    @Test
    void testPlayer() {
        DisplayBoard displayBoard = new DisplayBoard();
        RandomPlayer randomPlayer = new RandomPlayer(displayBoard);

        ArrayList<int[]> testGetMoves = new ArrayList<>();
        assertEquals(testGetMoves, randomPlayer.getMoves());
        randomPlayer.play();
        testGetMoves.add(new int[]{0, 0});
        assertArrayEquals(testGetMoves.get(0), randomPlayer.getMoves().get(0));
    }

}