import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    Board board = new Board(5, 5, 5);

    @org.junit.jupiter.api.Test
    void getNumberOfMines() {
        assertEquals(5, board.getNumberOfMines());
    }

    @org.junit.jupiter.api.Test
    void getBoardMatrix() {
        System.out.println(board.toString());
    }
}