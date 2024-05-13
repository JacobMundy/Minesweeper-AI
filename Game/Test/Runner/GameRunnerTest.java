package Runner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameRunnerTest {

    @Test
    void startGame() {
        GameRunner gameRunner = new GameRunner();
        assertDoesNotThrow(() -> gameRunner.startGame(false));
    }
}